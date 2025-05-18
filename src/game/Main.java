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
        Scanner scanner = new Scanner(System.in);
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


    public static void playGame(GameWorld world, PlayerCharacter player) {
        updateVisibility(world, player);
        GameFrame frame = GameWorld.getInstance().getGameFrame();
        frame.getMapPanel().updateMap();
        frame.getStatusPanel().updateStatus(player);


        PopupPanel.showPopup("Game Started", "Use the arrow keys to move your character.\nClick the Interact button to interact.");
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

    /**
     * Handles one player's turn:
     * - Moves the player and triggers interactions if successful
     */


    public static Position getDirectionFromUserAndCreatePosition(PlayerCharacter player, Scanner scanner) {
        System.out.print("Choose direction (w = up, s = down, a = left, d = right): ");
        String dir = scanner.nextLine().trim().toLowerCase();
        int newRow = player.getPosition().getRow();
        int newCol = player.getPosition().getCol();

        switch (dir) {
            case "w" -> newRow--;
            case "s" -> newRow++;
            case "a" -> newCol--;
            case "d" -> newCol++;
            default -> {
                System.out.println("Invalid direction.");
                return null;
            }
        }

        return new Position(newRow, newCol);
    }




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

    /**
     * Prints a 10x10 representation of the game board.
     * - Player is always displayed using the first character of their name.
     * - Other visible entities are shown based on their symbol.
     * - Hidden entities are not displayed.
     */
    public static void printMap(GameWorld world) {
        GameMap map = world.getMap();
        PlayerCharacter player = world.getPlayers().get(0);
        Position playerPos = player.getPosition();
        int size = 10;

        System.out.println("Current Map:");
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Position pos = new Position(row, col);
                List<GameEntity> entities = map.getEntitiesAt(pos);

                boolean printed = false;

                if (pos.equals(playerPos)) {
                    // מציג את השחקן
                    String symbol = player.getDisplaySymbol();
                    System.out.print("[" + symbol.charAt(0) + "]");
                    continue;
                }

                boolean hasVisibleEnemy = false;
                boolean hasWall = false;
                boolean hasVisibleTreasure = false;
                boolean hasVisiblePotion = false;
                boolean hasVisibleOther = false;

                for (GameEntity entity : entities) {
                    if (entity != null) {
                        boolean isClose = calcDistance(playerPos, entity.getPosition()) <= 2;

                        if (entity instanceof Enemy && (entity.isVisible() || isClose)) {
                            hasVisibleEnemy = true;
                        } else if (entity instanceof Wall && (entity.isVisible() || isClose)) {
                            hasWall = true;
                        } else if (entity instanceof Treasure && (entity.isVisible() || isClose)) {
                            hasVisibleTreasure = true;
                        } else if ((entity instanceof Potion || entity instanceof PowerPotion) && (entity.isVisible() || isClose)) {
                            hasVisiblePotion = true;
                        } else if (entity.isVisible() || isClose) {
                            hasVisibleOther = true;
                        }
                    }
                }

                if (hasVisibleEnemy) {
                    System.out.print("[E]");
                    printed = true;
                } else if (hasWall) {
                    System.out.print("[|]");
                    printed = true;
                } else if (hasVisibleTreasure) {
                    System.out.print("[T]");
                    printed = true;
                } else if (hasVisiblePotion) {
                    System.out.print("[P]");
                    printed = true;
                } else if (hasVisibleOther) {
                    System.out.print("[?]");
                    printed = true;
                }

                if (!printed) {
                    System.out.print("[ ]");
                }
            }
            System.out.println();
        }
    }



    /**
     * Prompts the player to choose between moving and interacting.
     * Repeats until a valid option (1 or 2) is selected.
     *
     * @param scanner Scanner to read input from player
     * @return GameAction.MOVE or GameAction.INTERACT
     */
    public static GameAction getChoosenAction(Scanner scanner) {
        while (true) {
            System.out.print("Choose action: 1 = MOVE, 2 = INTERACT ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    return GameAction.MOVE;
                case "2":
                    return GameAction.INTERACT;
                default:
                    System.out.println("Invalid input. Please enter 1 or 2.");
            }
        }
    }
}


