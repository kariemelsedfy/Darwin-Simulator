import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * This class represents a type of creature in a Darwin simulation. All
 * creatures of a given species execute the instructions of that species's
 * Darwin program, which specify how the creatures behave in the world. Each
 * Darwin program consists of a set of ordered instructions, each with an
 * instruction address (starting from address 0, like an index). At any given
 * time, each creature is at some instruction address, and continues executing
 * from that point the next time the creature acts. All creatures of a given
 * species are represented by a particular color.
 *
 * @author Karim ElSedfy
 * @version 1.0 11/21/2024
 */
public class Species {
    /**
     * Name of the species.
     */
    private String name;
    /**
     * Color of the species.
     */
    private Color color;
    /**
     * The list of the instructions of the species, ordered.
     */
    private ArrayList<Instruction> instructions;

    /**
     * An exception indicating that the given Species program file was malformed.
     */
    public static class BadSpeciesException extends RuntimeException {

        /**
         * Construct a new exception indicating that the species program was
         * malformed.
         * 
         * @param msg A message describing the problem.
         */
        public BadSpeciesException(String msg) {
            super(msg); // pass msg to parent constructor
        }

    }

    /**
     * Create a new species using the given Darwin program and the specified
     * color. May throw a BadSpeciesException if the given file does not exist or
     * does not contain a well-formed Darwin program.
     * 
     * @param filename The filename of a Darwin program.
     * @param color The color to use for this species.
     */
    public Species(String filename, Color color) {
        this.color = color;
        scanFile(filename);
    }

    /**
     * Reads the file, ignoring comments, and adding each instruction in the instructions list
     * by order. May throw a BadSpeciesException if the given file does not exist or
     * does not contain a well-formed Darwin program.
     *
     * @param filename The file to scan.
     */
    private void scanFile(String filename) {
        Scanner scan;
        ArrayList<String> instructionsString = new ArrayList<>();
        this.instructions = new ArrayList<>();
        try {
            scan = new Scanner(new File(filename));
        } catch (Exception e) {
            throw new BadSpeciesException("Failed to read file!");
        }
        while (scan.hasNext()) {
            String line = scan.nextLine();
            if((line.isEmpty() || (line.contains("#")))) {
                continue;
            }
            instructionsString.add(line);
        }
        this.name = instructionsString.get(0);
        instructionsString.remove(0);
        for(String instruction: instructionsString) {
            instruction = instruction.trim();
            if (instruction.endsWith(":")) {
                String label = instruction.substring(0, instruction.length() - 1);
                this.instructions.add(new Instruction(Opcode.LABEL, label));
            } else {
                String[] parts = instruction.split(" ");
                Opcode opcode = Opcode.fromString(parts[0]);
                if (opcode == null) {
                    throw new BadSpeciesException("Invalid opcode in file: " + parts[0]);
                }
                if (parts.length == 2) {
                    this.instructions.add(new Instruction(opcode, parts[1]));
                } else {
                    this.instructions.add(new Instruction(opcode));
                }
            }

        }
        scan.close();
    }

    /**
     * Get the name of the species.
     * 
     * @return The species name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the color of the species.
     * 
     * @return The species color.
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Get the number of instructions in the species program.
     * 
     * @return The number of species program instructions.
     */
    public int programSize() {
        return this.instructions.size();
    }

    /**
     * Get a particular instruction from the species program.
     * 
     * @param address The address of the desired instruction.
     * @return The specified instruction.
     */
    public Instruction programStep(int address) {
        return this.instructions.get(address);
    }

    /**
     * Get the address of the instruction within the species program corresponding
     * to the given label. Assumes that a label instruction with the given name
     * exists within the species program.
     * 
     * @param label The name of the label to lookup.
     * @return The instruction address of the given label.
     */
    public int getLabelAddress(String label) {
        for(Instruction instruction: this.instructions) {
            if(instruction.getLabel() == null) {
                continue;
            }
            if((instruction.getLabel().equals(label)) &&
                    (instruction.getOpcode().equals(Opcode.LABEL))) {
                return this.instructions.indexOf(instruction);
            }
        }
        //not found
        return 0;
    }

    /**
     * Construct a string representation of the species program in some reasonable
     * format. Useful for debugging.
     * 
     * @return A string representing the species program.
     */
    public String programToString() {
        String stringRepresentation = this.name + "'s color is" +
                this.color  + "\n Instructions: \n";
        for(Instruction instruction: this.instructions) {
            stringRepresentation += instruction.toString() + "\n";
        }
        return stringRepresentation;
    }

    /**
     * Tests the functionality of the Species class.
     */
    public static void main(String[] s) {
        Species flytrap = new Species("species/Kaiser.txt", Color.BLACK);
        System.out.println(flytrap.getName());
        System.out.println(flytrap.programSize());
        System.out.println(flytrap.programStep(3));
        System.out.println(flytrap.programToString());
    }

}
