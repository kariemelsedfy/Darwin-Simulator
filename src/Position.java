/**
 * This class represents an (x,y) coordinate in a Darwin world. The upper-left
 * square of the world is at position (0,0) and the lower-right square of a k by
 * k world is at position (k-1,k-1).
 *
 * @author Karim ElSedfy
 * @version 1.0 11/21/2024
 */
public class Position {

    /**
     * x: x-coordinate position.
     * y: y-coordinate position.
     */
    private int x, y;

    /**
     * Create a new position for the given x and y coordinates.
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get the x coordinate for the position.
     * 
     * @return The x coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Get the y coordinate for the position.
     * 
     * @return The y coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Get a new position that is adjacent in one of the four compass directions
     * from this position.
     * 
     * @param direction The compass direction to move.
     * @return The position that is one square in the given direction from this
     *         position.
     */
    public Position getAdjacent(Direction direction) {
        switch (direction) {
            case NORTH:
                return new Position(x, y - 1);
            case SOUTH:
                return new Position(x, y + 1);
            case EAST:
                return new Position(x + 1, y);
            case WEST:
                return new Position(x - 1, y);
            default:
                throw new IllegalStateException("bad direction " + direction);
        }
    }

    /**
     * Get a textual representation of the position.
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * Check if this position is equal to another object. Two positions are equal
     * if they have the same coordinates.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position) {
            Position p2 = (Position) obj;
            return x == p2.x && y == p2.y;
        }
        return false;
    }

}
