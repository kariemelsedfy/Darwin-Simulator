# Darwin Simulation Project

## Overview
The Darwin Simulation is a Java-based project that simulates interactions between creatures in a grid-based world. Each creature belongs to a specific species, and their behaviors are dictated by a program unique to their species. The simulation provides an engaging visualization of these interactions, demonstrating concepts in object-oriented programming and evolutionary dynamics.

## Features
- **Creatures**: Each creature represents an individual entity in the simulation with species-specific instructions.
- **World**: A dynamic 2D grid where creatures move, interact, and execute their behaviors.
- **Instruction Set**: Behavior is defined by opcodes such as `HOP`, `LEFT`, `RIGHT`, `INFECT`, and conditional jumps.
- **Visualization**: Graphical display of the simulation, showing creatures as colored arrows representing their species and direction.

## How It Works
1. **Species**: Each species is defined by a file containing its unique set of instructions.
2. **World**: The grid-based world is initialized, and creatures are placed at random positions.
3. **Execution**: Creatures execute their species-specific instructions, responding to the world around them and other creatures.
4. **Visualization**: A graphical interface dynamically updates to show creature movements and interactions.

## Requirements
- Java Development Kit (JDK) 8 or higher.

## Setup and Usage
1. **Clone the Repository**:
   ```
   git clone <repository_url>
   ```
2. **Compile the Code**:
   Navigate to the project directory and compile the Java files located in the `src` folder:
   ```
   javac src/*.java
   ```
3. **Run the Simulation**:
   Execute the `Darwin` class to start the simulation:
   ```
   java -cp src Darwin
   ```
4. **Provide Input**:
   - Enter species filenames and colors when prompted.
   - Observe the simulation in the graphical interface.

## Files
- **src/Creature.java**: Defines the behavior and properties of each creature.
- **src/World.java**: Represents the grid-based simulation environment.
- **src/Direction.java**: Manages compass directions (NORTH, SOUTH, EAST, WEST).
- **src/Instruction.java**: Represents individual instructions for species programs.
- **src/Opcode.java**: Defines valid operations (opcodes) for instructions.
- **src/Position.java**: Handles positions in the world grid.
- **src/Species.java**: Represents a species and its program logic.
- **src/WorldMap.java**: Provides graphical visualization for the simulation.
- **src/Darwin.java**: Entry point to initialize and run the simulation.

## Highlights
- **Object-Oriented Design**: Modular structure with well-defined responsibilities for each class.
- **Simulation Logic**: Combines conditional instructions and randomization for dynamic interactions.
- **Visual Representation**: Real-time visualization enhances understanding of creature behaviors.

## Note
This project was developed as part of a Bowdoin College Data Structures class. All Java source code files in the `src` directory were implemented by me. Supporting assets or additional files outside this directory were pre-existing or provided.

## Future Improvements
- Expand instruction set with more complex behaviors.
- Allow custom world sizes and configurations.
- Add logging or replay functionality.

## Author
- Karim ElSedfy

