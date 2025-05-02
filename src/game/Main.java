//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game;
import game.characters.*;
import game.combat.CombatSystem;
import game.combat.MagicElement;
import game.core.GameEntity;
import game.engine.GameWorld;
import game.gui.GameFrame;
import game.items.*;
import game.map.GameMap;
import game.map.Position;

import java.util.*;

// Enum for the possible actions the player can choose each turn
enum GameAction {
    MOVE, INTERACT
}
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GameWorld world = GameWorld.getInstance();// Singleton instance of the game world
        initializeGame(world, scanner);// Set up the game map and player

        PlayerCharacter player = world.getPlayers().get(0);// Get the player character
        GameFrame gf = new GameFrame(player);
        //playGame(world, player, scanner);
    }


    /**
     * Initializes the game:
     * - Prompts for board size
     * - Creates and sets a new game map
     * - Prepares the map with entities
     * - Prompts for player name and class and places them on the map
     */
    public static void initializeGame(GameWorld world, Scanner scanner) {
        int size = getBoardSize(scanner);
        world.setMap(new GameMap());
        prepareGame(world, size);

        System.out.print("Enter your player name: ");
        String name = scanner.nextLine();
        setPlayer(world, name, scanner);
    }

    /**
     * Main game loop: runs until the player dies.
     * Each turn:
     * - Updates visibility
     * - Prints the map
     * - Asks player for action
     * - Executes movement or interaction
     */
    public static void playGame(GameWorld world, PlayerCharacter player, Scanner scanner) {
        Random rand = new Random();
        int size = 10;

        System.out.println("\nThe game has started!");

        while (!player.isDead()) {
            updateVisibility(world, player);
            printMap(world);
            GameAction action = getChoosenAction(scanner);

            switch (action) {
                case MOVE -> {
                    //scanner.nextLine();
                    Position newPos = movePlayer(world, player, size);

                    if (newPos != null) {
                        //updateVisibility(world, player);
                        //printMap(world);
                        if (!player.isDead()) {
                            handleInteractions(world, player, newPos);
                            //updateVisibility(world, player);
                            //printMap(world);
                        }
                    } else {
                        System.out.println("Blocked by wall or invalid move.");
                    }
                }

                case INTERACT -> {
                    System.out.println("\n--- Interacting with current cell ---");
                    handleInteractions(world, player, player.getPosition());
                    //updateVisibility(world, player);
                    //printMap(world);
                }
            }
        }

        System.out.println(player.getName() + " has died. Game Over.");
    }



    /**
     * Makes visible all entities within 1 tile (Manhattan distance < 2) of the player.
     */
//    public static void updateVisibility(GameWorld world, PlayerCharacter player) {
//        GameMap map = world.getMap();
//        for (Map.Entry<Position, List<GameEntity>> entry: map.getGrid().entrySet()) {
//              Position p = entry.getKey();
//              List<GameEntity> entities = entry.getValue();
//
//            if(calcDistance(p, player.getPosition()) < 2) {
//                for(GameEntity entity: entities) {
//                    if (entity!=null)
//                    {
//                        entity.setVisible(true);
//                    }
//                }
//            }
//        }
//    }
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
    public static void handleTurn(GameWorld world, PlayerCharacter player, int size, int roll) {
        Position newPos = movePlayer(world, player, size);
        if (newPos != null) {
            System.out.println(" Moved to: " + newPos);
            handleInteractions(world, player, newPos);
        } else {
            System.out.println(" Blocked by wall. Turn skipped.");
        }
    }


    /**
     * Moves the player in the chosen direction if the target cell is valid.
     * Before moving, it interacts with any object in the target cell.
     * If the player dies during the interaction (e.g., combat), movement is canceled.
     *
     * @param world the current game world
     * @param player the player to move
     * @param size the board size (rows/cols)
     * @return the new position if moved successfully, null otherwise
     */
    public static Position movePlayer(GameWorld world, PlayerCharacter player, int size) {
        Scanner scanner = new Scanner(System.in);
        Position current = player.getPosition();
        GameMap map = world.getMap();

        System.out.print("Choose direction (w = up, s = down, a = left, d = right): ");
        String dir = scanner.nextLine().trim().toLowerCase();

        int newRow = current.getRow();
        int newCol = current.getCol();

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

        if (newRow < 0 || newRow >= size || newCol < 0 || newCol >= size) {
            System.out.println("Cannot move outside the board!");
            return null;
        }

        Position newPos = new Position(newRow, newCol);

        List<GameEntity> entitiesAtNewPos = map.getEntitiesAt(newPos);

        System.out.println("Trying to move to (" + newRow + "," + newCol + ")");
        for (GameEntity entity : entitiesAtNewPos) {
            if (entity != null)
                System.out.println("- " + entity.getClass().getSimpleName());
        }

        boolean hasWall = entitiesAtNewPos.stream()
                .anyMatch(e -> e instanceof Wall);
        if (hasWall) {
            System.out.println("Blocked by wall!");
            return null;
        }

        //handleInteractions(world, player, newPos);
        //if (player.isDead()) return null;

        map.removeEntity(current, player);
        player.setPosition(newPos);
        map.addEntity(newPos, player);

        return newPos;
    }


    /**
     * Handles all interactions between the player and entities located in the given position.
     * Interactions can be with Enemies, Potions, or Treasures.
     * If the player dies during combat, further interactions are skipped.
     */
    public static void handleInteractions(GameWorld world, PlayerCharacter player, Position pos) {
        List<GameEntity> entities = new ArrayList<>(world.getMap().getEntitiesAt(pos));

        // For debugging: print all entities at this position
        System.out.println("Entities at your current cell:");
        for (GameEntity e : entities) {
            if (e != null)
                System.out.println(" - " + e.getClass().getSimpleName());
        }

        for (GameEntity entity : entities) {
            if (entity == null) continue;

            // --- Enemy Interaction ---
            if (entity instanceof Enemy enemy) {
                System.out.println("\nEncountered enemy: " + enemy.getClass().getSimpleName());
                System.out.println("Player HP: " + player.getHealth() + "/100");
                System.out.println(enemy.getClass().getSimpleName() + " HP: " + enemy.getHealth() + "/100");

                CombatSystem.resolveCombat(player, enemy);

                System.out.println("--- After Combat ---");
                System.out.println("Player HP: " + player.getHealth() + "/100");

                if (!enemy.isDead()) {
                    System.out.println(enemy.getClass().getSimpleName() + " HP: " + enemy.getHealth() + "/100");
                } else {
                    System.out.println(enemy.getClass().getSimpleName() + " has been defeated!");
                    GameMap map = world.getMap();
                    map.removeEntity(pos, enemy);
                }

                if (player.isDead()) {
                    System.out.println("You have died in battle.");
                    break;
                }
            }

            // --- Potion Interaction ---
            else if (entity instanceof Potion potion) {
                System.out.println("\nYou found a potion!");
                int oldHp = player.getHealth();
                potion.interact(player);
                System.out.println("HP before potion: " + oldHp + "/100");
                System.out.println("HP after potion: " + player.getHealth() + "/100");
                GameMap map = world.getMap();
                map.removeEntity(pos, potion);
            }

            // --- Treasure Interaction ---
            else if (entity instanceof Treasure treasure) {
                System.out.println("\nYou found a treasure!");
                treasure.interact(player);
                GameMap map = world.getMap();
                map.removeEntity(pos, treasure);
            }

            // --- Other Entities ---
            else {
                System.out.println("\nNo interaction possible with: " + entity.getClass().getSimpleName());
            }
        }
    }


    /**
     * Prompts the user to enter a board size and ensures it's at least 10.
     *
     * @param scanner Scanner for user input.
     * @return A valid board size (minimum 10).
     */
    public static int getBoardSize(Scanner scanner) {
        System.out.print("Enter the board size (at least 10): ");
        int size = scanner.nextInt();
        scanner.nextLine();
        if (size < 10) {
            System.out.println("Invalid size. Defaulting to 10.");
            size = 10;
        }
        return size;
    }


    /**
     * Fills the game map with entities (enemies, items, walls or empty space)
     * using random chance logic.
     *
     * @param gameWorld The game world instance with a map to populate.
     * @param size      The size of the game board.
     */
    public static void prepareGame(GameWorld gameWorld, int size) {
        //GameEntity entity;
        for (int row  = 0; row  < size; row ++) {
            for (int col  = 0; col  < size; col ++) {
                Position p = new Position(row, col );
                GameEntity entity= getNewMapEntity(p);
                if (entity !=null){
                    entity.setPosition(p);
                }
                gameWorld.getMap().addEntity(p, entity);
            }
        }
    }


    /**
     * Randomly creates a new map entity (enemy, item, wall) for a given position,
     * based on pre-defined probabilities.
     *
     * @param pos The position for the entity.
     * @return A new GameEntity or null (empty cell).
     */
    public static GameEntity getNewMapEntity(Position pos) {
        Random rand = new Random();
        int chance = rand.nextInt(100); // מספר בין 0 ל-99

        if (chance < 40) {
            return null; // 40% סיכוי שלא יווצר כלום
        } else if (chance < 70) {
            // 30% סיכוי לאויב
            int enemyType = rand.nextInt(3);
            switch (enemyType) {
                case 0 -> { return new Dragon(50, pos); }
                case 1 -> { return new Orc(50, pos); }
                case 2 -> { return new Goblin(50, pos); }
            }
        } else if (chance < 80) {
            // 10% סיכוי לקיר
            return new Wall(pos);
        } else {
            // 20% סיכוי לשיקוי
            int potionChance = rand.nextInt(100); // 0-99
            if (potionChance < 75) {
                return new Potion(pos); // שיקוי חיים (75% מתוך ה-20%)
            } else {
                return new PowerPotion(pos); // שיקוי עוצמה (25% מתוך ה-20%)
            }
        }
        return null;
    }


    /**
     * Allows the player to choose a character (Warrior, Mage, or Archer), places
     * them in a random empty location on the map, and adds them to the world.
     *
     * @param gameWorld The main game world instance.
     * @param name      The name entered by the player.
     * @param scanner   Scanner for user input.
     */
    public static void setPlayer(GameWorld gameWorld, String name, Scanner scanner) {
        Random rand = new Random();
        GameMap map = gameWorld.getMap();

        System.out.println("Choose character: 1) Warrior, 2) Mage, 3) Archer");
        int choice = scanner.nextInt();
        scanner.nextLine();

        PlayerCharacter player = switch (choice) {
            case 1 -> new Warrior(name);
            case 2 -> new Mage(name);
            case 3 -> new Archer(name);
            default -> null;
        };

        if (player == null) {
            System.out.println("Invalid choice.");
            return;
        }

        Position pos;
        do {
            int row = rand.nextInt(10);
            int col = rand.nextInt(10);
            pos = new Position(row, col);
        } while (!map.getEntitiesAt(pos).isEmpty());

        player.setPosition(pos);
        player.setVisible(true);
        map.addEntity(pos, player);
        gameWorld.getPlayers().add(player);
        //map.addEntity(pos, new Potion(pos)); // DEBUG: adds a potion in the player's cell

        System.out.println("Player " + name + " placed at: (Row: " + (pos.getRow() + 1) + ", Col: " + (pos.getCol() + 1) + ")");
    }

    /**
     * Randomly selects one of the four magic elements.
     *
     * @return A random MagicElement (FIRE, ICE, LIGHTNING, or ACID)
     */
    // Returns a random element.
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
            System.out.print("Choose action: 1 = MOVE, 2 = INTERACT ➤ ");
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


