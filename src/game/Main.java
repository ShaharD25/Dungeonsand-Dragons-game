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
        clearLogFile();
        restartGame();
    }

    public static void restartGame()
    {
        //clearLogFile();
        //LogManager.log("Restart Game");
        GameWorld world = GameWorld.getNewWorld();// Singleton instance of the game world
        initializeGame();// Set up the game map and player
        PlayerCharacter player = world.getPlayers().get(0);// Get the player character
        GameFrame gf = new GameFrame(player);
        world.setGameFrame(gf);
        new GameController(world, player, gf.getMapPanel(), gf.getMapPanel());
        world.startGame();
    }

    /**
     * Initializes the game:
     * - Prompts for board size
     * - Creates and sets a new game map
     * - Prepares the map with entities
     * - Prompts for player name and class and places them on the map
     */
    public static void initializeGame() {
        int size = getBoardSize(); //
        LogManager.log("Initializing game with board size: " + size);
        GameWorld world = GameWorld.getInstance();// Singleton instance of the game world
        world.setMap(new GameMap(size));
        world.setPlayer(); //
        world.prepareGame(size);
    }




    public static void updateVisibility(GameWorld world, PlayerCharacter player) {
        GameMap map = world.getMap();
        Position playerPos = player.getPosition();

        for (Map.Entry<Position, List<GameEntity>> entry : map.getGrid().entrySet()) {
            for (GameEntity entity : entry.getValue()) {
                if (entity != null) {
                    entity.setVisible(false);

                    if (calcDistance(playerPos, entity.getPosition()) <= 2) {
                        entity.setVisible(true);
                        LogManager.log(player.getName() + "sees " +entity.getClass().getSimpleName() + " at " + entity.getPosition());
                    }
                }
            }
        }
    }




    public static void closeGame() {
        //clearLogFile();
        LogManager.log("Exit Game");
        LogManager.shutdown(); //
        System.exit(0);        //
    }

    public static void clearLogFile() {
        try (FileWriter writer = new FileWriter("game_log.txt", false)) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


