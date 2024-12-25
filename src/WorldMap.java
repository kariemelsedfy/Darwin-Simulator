import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This class provides functionality to draw the graphical world map during a
 * Darwin simulation. Before any other operations can be performed, the starting
 * map must be initialized by calling WorldMap.initialize(width, height).
 */
public class WorldMap {

    // singleton map to be operated on by the rest of the program
    private static WorldMapImpl map = null;

    // whether to synchronize immediately after redrawing
    private static boolean synchronize = true;

    /**
     * Initialize the world map of the specified size and display the window for
     * it. This must be called once and only once, before calling any other
     * WorldMap operations. Produces an error if called multiple times.
     * 
     * @param width
     *          The width of the world (1 to 30).
     * @param height
     *          The height of the world (1 to 30).
     */
    public static void initialize(int width, int height) {
        if (map != null) {
            throw new IllegalStateException("map already initialized");
        }
        map = new WorldMapImpl(width, height);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    /**
     * Draw the given creature at the square indicated by its current position.
     * Creatures are drawn as a colored arrow (of the species's color) with the
     * first letter of the species's name, pointing in the creature's current
     * direction. Any existing contents drawn at the square to update are
     * overwritten. This method does *not* affect any other square at which
     * the creature may have previously been drawn.
     * 
     * @param creature
     *          The creature to draw.
     */
    public static void drawCreature(Creature creature) {
        if (map == null) {
            throw new IllegalStateException(
                "map not created; call WorldMap.initialize");
        }
        Species species = creature.species();
        char letter = species.getName().charAt(0);
        map.displaySquareInst(creature.position(), letter, creature.direction(),
            species.getColor());
    }

    /**
     * Clear the contents of the square at the given position.
     * 
     * @param pos
     *          The position of the square to clear.
     */
    public static void clearSquare(Position pos) {
        if (map == null) {
            throw new IllegalStateException(
                "map not created; call WorldMap.initialize");
        }
        map.displaySquareInst(pos, ' ', null, null);
    }

    /**
     * Re-draw a creature that just moved. Functionally identical to calling
     * drawCreature(creature) followed by clearSquare(prevPos),
     * but produces a smoother transition with less image flicker. Produces an
     * error if the given creature is located at the same position as the
     * square to clear.
     * 
     * @param creature
     *          The creature to draw.
     * @param prevPos
     *          The position of the square to clear.
     */
    public static void drawMovedCreature(Creature creature, Position prevPos) {
        if (map == null) {
            throw new IllegalStateException(
                "map not created; call WordlMap.initialize");
        } else if (creature.position().equals(prevPos)) {
            throw new IllegalArgumentException(
                "creature position and prevPos are the same");
        } else if (prevPos == null) {
            throw new NullPointerException("prevPos cannot be null");
        }
        synchronize = false;
        clearSquare(prevPos);
        synchronize = true;
        drawCreature(creature);
    }

    /**
     * Pause the simulation for the given duration. This should be called
     * periodically to prevent the simulation from completing instantly.
     * 
     * @param durationMs
     *          The duration to pause, in milliseconds.
     */
    public static void pause(long durationMs) {
        try {
            Thread.sleep(durationMs);
        } catch (InterruptedException e) {
        }
    }

    /*
     * 
     * Everything else below is internal code to manage the window. You should not
     * need to modify, understand, or even look at it.
     * 
     */

    // private constructor - not for instantiation
    private WorldMap() {
    }

    // whether display commands are enabled
    private static boolean doDisplay = true;

    /**
     * Inner class for the panel so that everything is encapsulated in the
     * WorldMap class and not visible outside.
     */
    private static class WorldMapImpl extends JPanel {

        private static final long serialVersionUID = 0L;

        private static final int SQUARE_SIZE = 22;

        private static final int INSET = 10;

        private int width, height; // size of board

        private Cell[][] board; // the board

        private static final Font font = new Font("Roman", 0, 10);

        /**
         * Info about one cell on the board
         */
        private static class Cell {

            private char c = ' ';

            private Direction dir = null;

            private Color color = Color.BLACK;

        }

        private WorldMapImpl(int w, int h) {
            super(true);

            // Initialize drawing colors, border, opacity.
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);

            if (w > 30 || h > 30) {
                throw new IllegalArgumentException("only supports sizes up to 30x30");
            }
            width = w;
            height = h;
            board = new Cell[w][h];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    board[i][j] = new Cell();
                }
            }

            JFrame f = new JFrame("Darwin");
            f.setSize(new Dimension(2 * INSET + SQUARE_SIZE * (w + 1) + 10,
                    2 * INSET + SQUARE_SIZE * (h + 1) + 10));
            f.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                });

            f.getContentPane().add(this, BorderLayout.CENTER);
            f.setVisible(true);
            f.setResizable(false);
            repaint(0, 0, getWidth(), getHeight());
        }

        /*
         * Used to control synchronization between Darwin thread and AWT thread.
         */
        private final Object sem = new Object();

        /*
         * Store the new info in the board, and generate a repaint event. This
         * method will block until the repaint actually occurs.
         */
        private void displaySquareInst(Position pos, char c, Direction d, Color color) {
            if (!doDisplay) {
                return;
            }
            int x = pos.getX();
            int y = pos.getY();
            if (x < 0 || x >= width || y < 0 || y >= height) {
                throw new IllegalArgumentException("bad display square call");
            }
            board[x][y].c = c;
            board[x][y].dir = d;
            board[x][y].color = color;
            // to make sure all updates are seen in a timely fashion, we
            // wait on sem here. sem is notified after the refresh has
            // occured.
            synchronized (sem) {
                // dont't repaint over grid lines
                repaint(INSET + x * SQUARE_SIZE + 1, INSET + y * SQUARE_SIZE + 1,
                    SQUARE_SIZE - 2, SQUARE_SIZE - 2);

                if (synchronize) {
                    try {
                        // wait until repaint has occurred.
                        sem.wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        // Should never get here, but just try to
                        // keep going if anything goes wrong.
                        return;
                    }
                }
            }
        }

        private void drawGrid(Graphics g) {
            g.setColor(Color.BLACK);
            for (int i = 0; i <= width; i++) {
                g.drawLine(INSET + i * SQUARE_SIZE, INSET, INSET + i * SQUARE_SIZE,
                    INSET + height * SQUARE_SIZE);
            }
            for (int i = 0; i <= height; i++) {
                g.drawLine(INSET, INSET + i * SQUARE_SIZE, INSET + width * SQUARE_SIZE,
                    INSET + i * SQUARE_SIZE);
            }
        }

        /**
         * Draw one cell on the grid. Pass in the x and y grid position and the
         * contents of the cell.
         */
        private void drawSquare(Graphics g, int x, int y, Cell cell) {
            int sqX = INSET + x * SQUARE_SIZE + 1; // left edge of square
            int sqY = INSET + y * SQUARE_SIZE + 1; // right edge of square
            int fontX = 0; // x offset to put char in decent place for different dirs
            int fontY = 0; // y offset to put char in decent place for different dirs

            if (cell.c == ' ') {
                return;
            }

            g.setColor(cell.color);

            switch (cell.dir) {
                case NORTH: {
                    int[] x1Points = { sqX + 10, sqX + 18, sqX + 18, sqX + 2, sqX + 2 };
                    int[] y1Points = { sqY + 2, sqY + 10, sqY + 18, sqY + 18, sqY + 10 };
                    g.drawPolygon(x1Points, y1Points, x1Points.length);
                    fontX = 1;
                    fontY = 1;
                    break;
                }
                case WEST: {
                    int[] x1Points = { sqX + 2, sqX + 10, sqX + 18, sqX + 18, sqX + 10 };
                    int[] y1Points = { sqY + 10, sqY + 18, sqY + 18, sqY + 2, sqY + 2 };
                    g.drawPolygon(x1Points, y1Points, x1Points.length);
                    fontX = 4;
                    fontY = 2;
                    break;
                }
                case SOUTH: {
                    int[] x1Points = { sqX + 10, sqX + 18, sqX + 18, sqX + 2, sqX + 2 };
                    int[] y1Points = { sqY + 18, sqY + 10, sqY + 2, sqY + 2, sqY + 10 };
                    g.drawPolygon(x1Points, y1Points, x1Points.length);
                    fontX = 1;
                    fontY = 4;
                    break;
                }
                case EAST: {
                    int[] x1Points = { sqX + 18, sqX + 10, sqX + 2, sqX + 2, sqX + 10 };
                    int[] y1Points = { sqY + 10, sqY + 18, sqY + 18, sqY + 2, sqY + 2 };
                    g.drawPolygon(x1Points, y1Points, x1Points.length);
                    fontX = -1;
                    fontY = 2;
                    break;
                }
                default:
                throw new IllegalStateException("bad direction: " + cell.dir);
            }

            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            int height = fm.getHeight() - fontY;
            int width = fm.charWidth(cell.c) - fontX;

            g.drawString("" + cell.c, sqX + 10 - width / 2, sqY + 10 + height / 2);

        }

        /**
         * Clear what will be repainted, redraw grid if needed, and then draw any
         * squares in the clip area.
         */
        @Override
        protected void paintComponent(Graphics g) {

            // erase clip region
            super.paintComponent(g);

            // compute the grid squares that must be redrawn
            Shape rect = g.getClip();
            Rectangle bounds = rect.getBounds();
            int minX = Math.max(0, (int) ((bounds.getX() - INSET) / SQUARE_SIZE));
            int minY = Math.max(0, (int) ((bounds.getY() - INSET) / SQUARE_SIZE));
            int maxX = Math.min(width - 1,
                    (int) ((bounds.getX() + bounds.getWidth() - INSET) / SQUARE_SIZE));
            int maxY = Math.min(height - 1,
                    (int) ((bounds.getY() + bounds.getHeight() - INSET) / SQUARE_SIZE));

            // only redraw grid if clip overlaps one or more grid lines.
            if (minX != maxX || minY != maxY) {
                drawGrid(g);
            }

            for (int i = minX; i <= maxX; i++) {
                for (int j = minY; j <= maxY; j++) {
                    drawSquare(g, i, j, board[i][j]);
                }
            }

            synchronized (sem) {
                // wake up Darwin thread
                sem.notify();
            }

        }
    }

}
