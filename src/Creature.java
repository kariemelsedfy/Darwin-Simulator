import java.awt.*;
import java.util.Random;
/**
 * This class represents one creature in a Darwin simulation. Each creature is
 * of a particular species and has a position and direction within the
 * simulation world. In addition, each creature must remember its current
 * position within its species program, which tells it which instruction to
 * execute next. Lastly, creatures are responsible for drawing themselves in the
 * graphical world map and updating the map appropriately as actions are taken.
 *
 * @author Karim ElSedfy
 * @version 1.0 11/21/2024
 */
public class Creature {
    /**
     * Type of species of the creature instance.
     */
    private Species species;
    /**
     * The world in which the creature is placed.
     */
    private World world;
    /**
     * Creature position in the world instance.
     */
    private Position position;
    /**
     * Direction of the species.
     */
    private Direction direction;
    /**
     * The index of the current instruction.
     */
    private int step;

    /**
     * Constructor to Create a creature of the given species, within the given world, with the
     * indicated starting position and direction.
     *
     * @param species The species of the creature.
     * @param world The world to place the creature in.
     * @param pos The position to place the creature at.
     * @param dir The direction of the creature.
     */
    public Creature(Species species, World world, Position pos, Direction dir) {
        this.species = species;
        this.direction = dir;
        this.position = pos;
        this.step = 0;
        world.set(pos, this);
        this.world = world;
    }

    /**
     * Get the current species of the creature.
     * 
     * @return The current creature species.
     */
    public Species species() {
        return this.species;
    }

    /**
     * Get the current direction of the creature.
     * 
     * @return The current creature direction.
     */
    public Direction direction() {
        return this.direction;
    }

    /**
     * Get the current position of the creature.
     * 
     * @return The current creature position.
     */
    public Position position() {
        return this.position;
    }


    /**
     * Set the species of the creature.
     *
     * @param species The type species to set.
     */
    public void setSpecies(Species species) {
        this.species = species;
    }

    /**
     * Set the step of the creature.
     *
     * @param step The step, or in other words current instruction index, to set.
     */
    public void setStep(int step) {
        this.step = step;
    }


    /**
     * Repeatedly execute instructions from the creature's program until one of
     * the 'terminating' instructions (hop, left, right, or infect) is executed.
     */
    public void execute() {
        Instruction currentInstruction = this.species().programStep(this.step);
        if(currentInstruction.getOpcode().acceptsLabel()) {
            labelInstructionExecute(currentInstruction);
        } else {
            terminatingInstructionExecute(currentInstruction);
        }
    }

    /**
     * Handles an instruction that has a label. Has a switch statement considering all the
     * instructions that can have labels.
     *
     * Calls execute in every case except if the instruction is INFECT, for whom it terminates.
     *
     * @param instruction The current instruction.
     */
    public void labelInstructionExecute(Instruction instruction) {
        switch(instruction.getOpcode()) {
            case LABEL:
                break;
            case INFECT:
                infect(instruction);
                return;
            case IFEMPTY:
                if(isAdjacentNullAndNotWall()) {
                    goToInstructionLabelIndex(instruction);
                }
                break;
            case IFWALL:
                if(!world.inBounds(this.position.getAdjacent(this.direction))) {
                    goToInstructionLabelIndex(instruction);
                }
                break;
            case IFSAME:
                if(isSameSpeciesUpfront()) {
                    goToInstructionLabelIndex(instruction);
                }
                break;
            case IFENEMY:
                if(isEnemyUpfront()) {
                    goToInstructionLabelIndex(instruction);
                }
                break;
            case IFRANDOM:
                Random rand = new Random();
                if(rand.nextBoolean()) {
                    goToInstructionLabelIndex(instruction);
                }
                break;
            case GO:
                goToInstructionLabelIndex(instruction);
                break;
         }
        this.step++;
        execute();
    }

    /**
     * Handles terminating instructions (HOP, LEFT, RIGHT). Has a switch statement
     * considering all the instructions that can't have labels.
     *
     * Draws the creature after every case.
     *
     * @param instruction The current instruction.
     */
    public void terminatingInstructionExecute(Instruction instruction) {
        switch(instruction.getOpcode()) {
            case HOP:
                hop();
                break;
            case LEFT:
                this.direction = direction.left();
                break;
            case RIGHT:
                this.direction = direction.right();
                break;
        }
        this.step++;
        WorldMap.drawCreature(this);
    }


    /**
     * Checks if the target is not null and in bounds. If true, infects the target by changing
     * its species.
     *
     * If the instruction argument has a label, it makes the step of the infected creature
     * that label. If it doesn't it sets the step to 0. Increments step, Draws current, and
     * infected creature in case of infection.
     *
     * @param instruction The current instruction.
     */
    public void infect(Instruction instruction) {
        Position adjacent = this.position.getAdjacent(this.direction);
        if(isEnemyUpfront()) {
            world.get(adjacent).setSpecies(this.species);
            world.get(adjacent).setStep(0);
            if(instruction.getLabel() != null) {
                world.get(adjacent).
                        setStep(this.species.getLabelAddress(instruction.getLabel()));
            }
            WorldMap.drawCreature(world.get(adjacent));
        }
        WorldMap.drawCreature(this);
        this.step++;
    }


    /**
     * Checks if the target/adjacent square is in bounds and empty/null. Moves the creature to that
     * square and clears the previous square. Calls DrawMovedCreature on the current creature.
     */
    public void hop() {
        Position previousPosition = this.position;
        Position prospectivePosition = this.position.getAdjacent(this.direction);
        if(((world.inBounds(prospectivePosition)) &&
                (world.get(prospectivePosition) == null))){
            world.set(prospectivePosition, this);
            this.position = prospectivePosition;
            world.set(previousPosition, null);
            WorldMap.drawMovedCreature(this, previousPosition);
        }
    }

    /**
     * Checks if the adjacent square is in bounds, not empty, the same species as this creature.
     *
     * @return Is there a creature of the same species in the adjacent square.
     */
    public boolean isSameSpeciesUpfront() {
        Position adjacent = this.position.getAdjacent(this.direction);
        if(!world.inBounds(adjacent)) {
            return false;
        }
        Creature adjacentCreature = world.get(adjacent);
        return (adjacentCreature != null) && (adjacentCreature.species().equals(this.species));
    }

    /**
     * Checks if the adjacent square is in bounds, not empty, not the same species as this creature.
     *
     * @return Is there a creature not of the same species in the adjacent square.
     */
    public boolean isEnemyUpfront() {
        Position adjacent = this.position.getAdjacent(this.direction);
        if(!world.inBounds(adjacent)) {
            return false;
        }
        Creature adjacentCreature = world.get(adjacent);
        return (adjacentCreature != null) && (!adjacentCreature.species().equals(this.species));
    }

    /**
     * Checks if the adjacent square not null and inbounds.
     *
     * @return Is the adjacent square not null and inbounds.
     */
    private boolean isAdjacentNullAndNotWall() {
        Position adjacent = this.position.getAdjacent(this.direction);
        Creature adjacentCreature = world.get(adjacent);
        return (world.inBounds(adjacent)) && (adjacentCreature == null);
    }

    /**
     * Sets step of current creature to the address of the label of instruction minus one. This is
     * done because labelInstructionExecute automatically increments the step after for every
     * instruction with a label.
     *
     * @param instruction The current instruction.
     */
    public void goToInstructionLabelIndex(Instruction instruction) {
        this.step = this.species.getLabelAddress(instruction.getLabel()) - 1;
    }



    /**
     * Tests the functionality of the Creature class.
     */
    public static void main(String[] args) {
        Species kaiser = new Species("species/Kaiser.txt", new Color(0, 0, 0));
        Species food = new Species("species/Food.txt", new Color(120, 120, 120));
        World world = new World(15, 15);
        Creature creatureKaiser = new Creature(kaiser, world,
                new Position(2, 10), Direction.NORTH);
        Creature creatureFood = new Creature(food, world,
                new Position(2, 0), Direction.random());
        WorldMap.initialize(15, 15);
        WorldMap.drawCreature(creatureKaiser);
        WorldMap.drawCreature(creatureFood);
        while(true) {
            creatureKaiser.execute();
            WorldMap.pause(1000);
        }
    }

}
