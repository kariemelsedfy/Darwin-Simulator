/**
 * This class represents one Darwin instruction. Instructions contain two parts:
 * an opcode and (depending on the opcode) a label. See the Opcode class for a
 * listing of the instruction types.
 */
public class Instruction {

    /**
     * The opcode of the instruction.
     */
    private Opcode opcode;

    /**
     * The instruction label or null.
     */
    private String label;

    /**
     * Create a new instruction consisting only of an opcode. Throws an exception
     * if the given opcode requires a label.
     * 
     * @param opcode The opcode of the instruction.
     */
    public Instruction(Opcode opcode) {
        if (opcode == null) {
            throw new NullPointerException("missing opcode");
        } else if (opcode.labelRequired()) {
            throw new IllegalArgumentException("opcode " + opcode + " needs label");
        }
        this.opcode = opcode;
        this.label = null;
    }

    /**
     * Create a new instruction consisting of both an opcode and a label. Throws
     * an exception if the given opcode cannot accept a label.
     * 
     * @param opcode The opcode of the instruction.
     * @param label The label of the instruction.
     */
    public Instruction(Opcode opcode, String label) {
        if (opcode == null) {
            throw new NullPointerException("missing opcode");
        } else if (label == null) {
            throw new NullPointerException("missing label");
        } else if (!opcode.acceptsLabel()) {
            throw new IllegalArgumentException(
                "opcode " + opcode + " cannot take label");
        }
        this.opcode = opcode;
        this.label = label;
    }

    /**
     * Get the opcode of the instruction.
     * 
     * @return The instruction opcode.
     */
    public Opcode getOpcode() {
        return opcode;
    }

    /**
     * Get the label of the instruction (if one exists).
     * 
     * @return The instruction label, or null.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Get a string representation of the instruction in the form used in a Darwin
     * species file.
     */
    @Override
    public String toString() {
        if (opcode == Opcode.LABEL) {
            return label + ":";
        } else if (label != null) {
            return opcode + " " + label;
        } else {
            return opcode.toString();
        }
    }

}
