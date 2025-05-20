//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game;
import game.audio.SoundPlayer;
import game.characters.*;
import game.combat.CombatSystem;
import game.combat.MagicElement;
import game.controller.GameController;
import game.core.GameEntity;
import game.engine.GameWorld;
import game.gui.GameFrame;
import game.gui.PopupPanel;
import game.items.*;
import game.map.GameMap;
import game.map.Position;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

// Enum for the possible actions the player can choose each turn
enum GameAction {
    MOVE, INTERACT
}
public class Main {
    public static void main(String[] args) {
        GameWorld world = GameWorld.getInstance();// Singleton instance of the game world
        restartGame(world);
    }

    public static void restartGame(GameWorld world)
    {
        initializeGame(world);// Set up the game map and player
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
    public static void initializeGame(GameWorld world) {
        int size = getBoardSize(); //
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
                    }
                }
            }
        }
    }


    /**
     * Returns the Manhattan distance between two positions.
     */
    public static int calcDistance(Position p1, Position p2) {
        return Math.abs(p1.getRow() - p2.getRow()) + Math.abs(p1.getCol() - p2.getCol());}






    public static int getBoardSize() {
        int size = 10;
        while (true) {
            String input = JOptionPane.showInputDialog(null,
                    "Enter the board size (at least 10):", "Board Size", JOptionPane.QUESTION_MESSAGE);

            if (input == null) {
                JOptionPane.showMessageDialog(null, "No input provided. Defaulting to 10.");
                break;
            }

            try {
                size = Integer.parseInt(input);
                if (size >= 10) break;
                else JOptionPane.showMessageDialog(null, "Size must be at least 10.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        }
        return size;
    }



    /**
     * Randomly selects one of the four magic elements.
     *
     * @return A random MagicElement (FIRE, ICE, LIGHTNING, or ACID)
     */
    public static MagicElement getRandomElement()
    {
        Random rand = new Random();
        int randInt = rand.nextInt(4);

        switch (randInt) {
            case 0: return MagicElement.FIRE;
            case 1: return MagicElement.ICE;
            case 2: return MagicElement.LIGHTNING;
            default: return MagicElement.ACID;
        }
    }

}


