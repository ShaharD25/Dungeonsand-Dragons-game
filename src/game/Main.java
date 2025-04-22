package game;

import game.characters.*;
import game.combat.CombatSystem;
import game.combat.MagicElement;
import game.core.GameEntity;
import game.engine.GameWorld;
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
        playGame(world, player, scanner);
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
                    System.out.println("\n--- Moving ---");
                    System.out.println("Press ENTER to roll the dice...");
                    scanner.nextLine();
                    int roll = rand.nextInt(10) + 1;
                    System.out.println(player.getName() + " rolled: " + roll);
                    handleTurn(world, player, size, roll);
                }
                case INTERACT -> {
                    System.out.println("\n--- Interacting with current cell ---");
                    handleInteractions(world, player, player.getPosition());
                }
            }
        }
        System.out.println( player.getName() + " has died. Game Over.");
    }


    /**
     * Makes visible all entities within 1 tile (Manhattan distance < 2) of the player.
     */
    public static void updateVisibility(GameWorld world, PlayerCharacter player) {
        GameMap map = world.getMap();
        for (Map.Entry<Position, List<GameEntity>> entry: map.getGrid().entrySet()) {
              Position p = entry.getKey();
              List<GameEntity> entities = entry.getValue();

            if(calcDistance(p, player.getPosition()) < 2) {
                for(GameEntity entity: entities) {
                    if (entity!=null)
                    {
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

        // Invalid position check
        if (!map.isValidPosition(newPos) || !map.getEntitiesAt(newPos).isEmpty()) {
            System.out.println("Can't move there (either wall or occupied).");
            return null;
        }
        // Handle walls
        boolean hasWall = map.getEntitiesAt(newPos).stream()
                .anyMatch(e -> e instanceof Wall);
        if (hasWall) {
            System.out.println("Blocked by wall!");
            return null;
        }

        handleInteractions(world, player, newPos);// Perform interaction before moving
        if (player.isDead()) return null; // Check if the player is dead after interaction

        // Remove player from old location and add to new one
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

            // Enemy interaction: combat is initiated
            if (entity instanceof Enemy enemy) {
                System.out.println("Encountered enemy: " + enemy.getClass().getSimpleName());
                CombatSystem.resolveCombat(player, enemy);

                if (player.isDead()) {
                    System.out.println(" You have died in battle.");
                    break; // Stop further interactions if dead
                }
            }

            // Potion interaction
            else if (entity instanceof Potion potion) {
                System.out.println(" You found a potion.");
                potion.interact(player);
            }
            // Treasure interaction
            else if (entity instanceof Treasure treasure) {
                System.out.println("You found a treasure!");
                treasure.interact(player);
            }
            // If it's a wall or unhandled object — ignore it
            else {
                System.out.println("No interaction possible with: " + entity.getClass().getSimpleName());
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
        GameEntity entity;
        for (int row  = 0; row  < size; row ++) {
            for (int col  = 0; col  < size; col ++) {
                Position p = new Position(row, col );
                entity= getNewMapEntity(p);
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
    public static GameEntity getNewMapEntity(Position pos)
    {
        Random rand = new Random();
        if(rand.nextInt(4) == 0) {
            // 40% return nothing.
            return null;
        }

        if(rand.nextInt(3) == 0) {
            // 30% return enemy.
            int loot = rand.nextInt(50) + 50; // TODO
            int enemyRand = rand.nextInt(3);
            switch (enemyRand) {
                case 0: return new Dragon(loot, pos);
                case 1: return new Orc(loot, pos);
                case 2: return new Goblin(loot, pos);
            }
        }

        // Return wall
        if(rand.nextInt(10) == 0) {
            // 10% return wall.
            return new Wall(pos);
        }

        // Return Potion
        if(rand.nextInt(15) == 0) {
            // 15% return potion.
            return new Potion(pos);
        }

        // Return Power Potion.
        return new PowerPotion(pos);
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
            for (int col  = 0; col  < size; col ++) {
                Position pos = new Position(row, col );
                List<GameEntity> entities = map.getEntitiesAt(pos);

                boolean printed = false;
                if (pos.equals(playerPos)) {
                    String symbol = player.getDisplaySymbol();
                    System.out.print("[" + symbol.charAt(0) + "]");
                    continue;
                }

                for (GameEntity entity : entities) {
                    if (entity != null && entity.isVisible()) {
                        System.out.print("[" + entity.getDisplaySymbol().charAt(0) + "]");
                        printed = true;
                        break;
                    }
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


