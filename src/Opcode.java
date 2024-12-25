/**
 * This class represents one of the valid operations (opcodes) that are part of
 * the instructions of a Darwin species. Some operations take a label in
 * addition to the opcode itself.
 */
public enum Opcode {

    /**
     * Move forward
     */
    HOP(Type.NO_LABEL),
    
    /**
     * Turn left
     */
    LEFT(Type.NO_LABEL),
    
    /**
     * Turn right
     */
    RIGHT(Type.NO_LABEL),
    
    /**
     * Infect enemy
     */
    INFECT(Type.LABEL_OPTIONAL),
    
    /**
     * Jump to label if forward square is empty
     */
    IFEMPTY(Type.LABEL_REQUIRED),
    
    /**
     * Jump to label if forward square is a wall
     */
    IFWALL(Type.LABEL_REQUIRED),
    
    /**
     * Jump to label if forward square is an ally
     */
    IFSAME(Type.LABEL_REQUIRED),
    
    /**
     * Jump to label if forward square is an enemy
     */
    IFENEMY(Type.LABEL_REQUIRED),
    
    /**
     * Random jump to label
     */
    IFRANDOM(Type.LABEL_REQUIRED),
    
    /**
     * Unconditional jump to label
     */
    GO(Type.LABEL_REQUIRED),
    
    /**
     * Jump target (i.e., a label)
     */
    LABEL(Type.LABEL_REQUIRED);

    // an opcode type (argument behavior) - only for internal use
    private static enum Type {
        NO_LABEL, LABEL_REQUIRED, LABEL_OPTIONAL
    }

    /**
     * Convert a string (e.g., 'hop') to its corresponding opcode (e.g.,
     * Opcode.HOP). The given string is case-insensitive, but otherwise must
     * exactly match an opcode value.  This is a slightly more user-friendly
     * wrapper for the valueOf method that is automatically generated for
     * all enum types.
     * 
     * @param str The string of an opcode value.
     * @return The corresponding opcode, or null if no such opcode exists.
     */
    public static Opcode fromString(String str) {
        try {
            return valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            // unrecognized opcode
            return null;
        }
    }

    // the type of the opcode
    private final Type type;

    // construct a new opcode
    private Opcode(Type type) {
        this.type = type;
    }

    /**
     * Get whether the opcode accepts a label (either optional or required).
     * 
     * @return True iff the opcode accepts a label.
     */
    public boolean acceptsLabel() {
        return type != Type.NO_LABEL;
    }

    /**
     * Get whether the opcode accepts an optional label.
     * 
     * @return True iff the opcode accepts an optional label.
     */
    public boolean labelOptional() {
        return type == Type.LABEL_OPTIONAL;
    }

    /**
     * Get whether the opcode must be given a label.
     * 
     * @return True iff the opcode requires a label.
     */
    public boolean labelRequired() {
        return type == Type.LABEL_REQUIRED;
    }

    /**
     * Get a string representation of the opcode.
     */
    @Override
    public String toString() {
        return name().toLowerCase();
    }

}
