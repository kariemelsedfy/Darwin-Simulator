import java.awt.*;
import java.util.Random;
/**
 * This class represents the two-dimensional world during a Darwin simulation.
 * Each position of the world may be populated either by nothing or by a single
 * creature.
 *
 * @author Karim ElSedfy
 * @version 1.0 11/21/2024
 */
public class World {
    /**
     * The width of the world
     */
    private int width;
    /**
     * The height of the world.
     */
    private int height;
    /**
     * 2D array representing the grid of Creatures. The element will be null if it's empty.
     */
    private Creature[][] grid;

    /**
     * Create a new world consisting of width columns and height rows. Initially,
     * the world contains no creatures.
     * 
     * @param width The width of the world.
     * @param height The height of the world.
     */
    public World(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Creature[width][height];
    }

    /**
     * Get the height of the world.
     * 
     * @return The world height.
     */
    public int height() {
        return this.height;
    }

    /**
     * Get the width of the world.
     * 
     * @return The world width.
     */
    public int width() {
        return this.width;
    }

    /**
     * Check whether the given position is within the bounds of the world (i.e.,
     * its x and y coordinates specify a valid world position).
     * 
     * @param pos The position to check.
     * @return Whether the given position is within the world bounds.
     */
    public boolean inBounds(Position pos) {
        return (pos.getX() >= 0) && (pos.getX() < this.width) &&
                (pos.getY() >= 0) && (pos.getY() < this.width);
    }

    /**
     * Get a random position within the bounds of the world. All valid world
     * positions (occupied or not) are returned with equal probability.
     * 
     * @return A random position within the world.
     */
    public Position randomPosition() {
        Random rand = new Random();
        int x = rand.nextInt(0, this.width);
        int y = rand.nextInt(0, this.height);
        return new Position(x, y);
    }

    /**
     * Update the given world position to contain the given creature (which may be
     * null, in which case the world position is cleared).
     * 
     * @param pos The position to update.
     * @param creature The creature to place at the given position, or null.
     */
    public void set(Position pos, Creature creature) {
        if(inBounds(pos)) {
            this.grid[pos.getX()][pos.getY()] = creature;
        }
    }

    /**
     * Get the creature at the given position of the board, or null if no creature
     * occupies that position.
     * 
     * @param pos The position to get.
     * @return The creature at the specified position, or null.
     */
    public Creature get(Position pos) {
        return this.grid[pos.getX()][pos.getY()];
    }
    
    /**
     * Tests the functionality of the World class.
     */
    public static void main(String[] args) {
        World world = new World(6, 6);
        System.out.println("World created with width: " + world.width() + " and height: " +
                world.height());
        Position pos1 = new Position(2, 2);
        Position pos2 = new Position(24, 7);
        System.out.println("pos1 inBounds?" + world.inBounds(pos1));
        System.out.println("pos2 inBounds?" + world.inBounds(pos2));
        Creature creature1 = new Creature(new Species("species/Flytrap.txt", Color.BLACK),
                world, new Position(3, 4), Direction.random());
        world.set(pos1, creature1);
        System.out.println("Creature placed at (3,4): " + (world.get(pos1).equals(creature1)));
        world.set(pos1, null);
        System.out.println("Position (2,2) cleared: " + (world.get(pos1) == null));
        Position randomPos = world.randomPosition();
        System.out.println("Random position: (" + randomPos.getX() + ", " + randomPos.getY()
                + ")");
        System.out.println("Random position in bounds: " + world.inBounds(randomPos));
    }
}
