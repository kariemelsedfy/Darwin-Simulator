import java.awt.Color;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Scanner;
import java.util.ArrayList;
/**
 * This class runs a Darwin simulation.
 *
 * @author Karim ElSedfy
 * @version 1.0 11/21/2024
 */
public class Darwin {
    /**
     * Get the Color specified by the given name. For a list of valid
     * color names, see the JavaDoc for the Color class.
     *
     * @param colorName The name of the color to lookup.
     * @return The specified color, or null if no such color exists.
     */
    private static Color colorFromString(String colorName) {
        try {
            Field field = Color.class.getField(colorName);
            return (Color) field.get(null);
        } catch (Exception e) {
            return null; // no such color
        }
    }


    /**
     *  Prompt for species filenames and colors, generate the starting world and
     *  creatures, then run the Darwin simulation.
     *
     *  Keeps looping asking for file names and color until the file name is blank, then executes.
     *
     *  Loops infinitely, shuffling the order of all the creatures before running execute on each
     *  creature. Pauses 500 milliseconds between loops.
     *
     * @param args Arguments for main function.
     */
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        World world = new World(15, 15);
        WorldMap.initialize(15, 15);
        ArrayList<Creature> creatures = new ArrayList<>();
        //Reading the files and initializing the creatures.
        while(true) {
            System.out.print("Enter a species filename: ");
            String speciesName = scan.nextLine();
            if(speciesName.isEmpty()) {
                break;
            }
            System.out.print("Enter color: ");
            String speciesColor = scan.nextLine();
            Species species;
            try {
                species = new Species(speciesName, colorFromString(speciesColor));
            } catch (Species.BadSpeciesException e) {
                System.out.println(e);
                continue;
            }
            for(int i = 0; i < 10; i++) {
                Position randomPosition = world.randomPosition();
                while(world.get(randomPosition) != null) {
                    randomPosition = world.randomPosition();
                }
                creatures.add(new Creature(species, world,
                        randomPosition, Direction.random()));
            }
        }
        for(Creature creature: creatures) {
            WorldMap.drawCreature(creature);
        }
        //start executing
        while(true) {
            Collections.shuffle(creatures);
            for(Creature creature: creatures) {
                creature.execute();
            }
            WorldMap.pause(500);
        }
    }

}
