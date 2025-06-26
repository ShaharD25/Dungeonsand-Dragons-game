//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game;

import game.characters.*;
import game.controller.GameController;
import game.core.GameEntity;
import game.engine.GameWorld;
import game.gui.GameFrame;
import game.map.GameMap;
import game.map.Position;
import game.logging.LogManager;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static game.map.GameMap.calcDistance;
import static game.map.GameMap.getBoardSize;

// Enum for the possible actions the player can choose each turn
enum GameAction {
    MOVE, INTERACT
}

public class Main {

    public static void main(String[] args) {
        clearLogFile();       // Clear the game log file before starting
        restartGame();        // Start the game
    }

    /**
     * Restarts the game by creating a new world,
     * initializing the map and player,
     * launching the game GUI,
     * and starting the game logic.
     */
    public static void restartGame() {
        GameWorld world = GameWorld.getNewWorld(); // Create a fresh game world instance
        initializeGame();                          // Prepare map, enemies, and player
        PlayerCharacter player = world.getPlayers().get(0); // Get the player

        GameFrame gf = new GameFrame(player);      // Create and show the game GUI
        world.setGameFrame(gf);                    // Set the frame in the game world
        new GameController(world, gf.getMapPanel(), gf.getMapPanel());

        // Start the game logic *after* the GUI is ready, to avoid premature enemy actions
        javax.swing.SwingUtilities.invokeLater(() -> {
            world.startGame();                     // Enemies start acting only after UI is ready
        });
    }

    /**
     * Initializes the game:
     * - Prompts the user for board size
     * - Creates a new map and assigns it to the world
     * - Places the player and enemies on the map
     */
    public static void initializeGame() {
        int size = getBoardSize(); // Ask user for board size
        LogManager.log("Initializing game with board size: " + size);

        GameWorld world = GameWorld.getInstance(); // Access singleton instance
        world.setMap(new GameMap(size));           // Create and assign the map
        world.setPlayer();                         // Set up the player
        world.prepareGame(size);                   // Add enemies and items
    }

    /**
     * Updates the visibility of entities on the map
     * based on the player's current position.
     */
    public static void updateVisibility(GameWorld world, PlayerCharacter player) {
        GameMap map = world.getMap();
        Position playerPos = player.getPosition();

        for (Map.Entry<Position, List<GameEntity>> entry : map.getGrid().entrySet()) {
            for (GameEntity entity : entry.getValue()) {
                if (entity != null) {
                    entity.setVisible(false);

                    // Make visible only entities within a radius of 2 tiles
                    if (calcDistance(playerPos, entity.getPosition()) <= 2) {
                        entity.setVisible(true);
                        LogManager.log(player.getName() + " sees " +
                                entity.getClass().getSimpleName() + " at " + entity.getPosition());
                    }
                }
            }
        }
    }

    /**
     * Shuts down the game safely.
     */
    public static void closeGame() {
        LogManager.log("Exit Game");
        LogManager.shutdown(); // Flush and close logs
        System.exit(0);        // Exit application
    }

    /**
     * Clears the content of the game log file.
     */
    public static void clearLogFile() {
        try (FileWriter writer = new FileWriter("game_log.txt", false)) {
            // Empty file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
