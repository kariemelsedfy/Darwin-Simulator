import java.util.Random;

/**
 * This class represents a compass direction (north, south, east, or west).
 */
public enum Direction {

    NORTH, SOUTH, EAST, WEST;

    private static final Random rand = new Random();

    /**
     * Get a direction selected uniformly at random.
     * 
     * @return A random direction.
     */
    public static Direction random() {
        switch (rand.nextInt(Direction.values().length)) {
            case 0:
                return NORTH;
            case 1:
                return SOUTH;
            case 2:
                return EAST;
            case 3:
                return WEST;
            default:
                throw new IllegalStateException("bad random result");
        }
    }

    /**
     * Get the direction to the right of this direction.
     * 
     * @return The direction to the right.
     */
    public Direction right() {
        switch (this) {
            case NORTH:
                return EAST;
            case EAST:
                return SOUTH;
            case SOUTH:
                return WEST;
            case WEST:
                return NORTH;
            default:
                throw new IllegalStateException("unhandled direction " + this);
        }
    }

    /**
     * Get the direction to the left of this direction.
     * 
     * @return The direction to the left.
     */
    public Direction left() {
        switch (this) {
            case NORTH:
                return WEST;
            case WEST:
                return SOUTH;
            case SOUTH:
                return EAST;
            case EAST:
                return NORTH;
            default:
                throw new IllegalStateException("unhandled direction " + this);
        }
    }

}
