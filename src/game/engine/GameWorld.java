//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.engine;
import game.characters.*;
import game.core.GameEntity;
import game.gui.GameFrame;
import game.items.GameItem;
import game.items.Potion;
import game.items.PowerPotion;
import game.items.Wall;
import game.map.GameMap;
import game.map.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import game.gui.GameObserver;

import javax.swing.*;


/**
 * Singleton class that holds all core components of the game state:
 * - players
 * - enemies
 * - items
 * - the map
 */
public class GameWorld {
    private List<PlayerCharacter> players;
    private List<Enemy> enemies;
    private List<GameItem> items;
    private GameMap map;
    private static GameWorld instance;

    private GameFrame gameFrame;
    private static final AtomicBoolean gameRunning = new AtomicBoolean(true);
    public static final ReentrantLock BOARD_LOCK = new ReentrantLock(true);
    private static final ScheduledExecutorService EXEC = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * Private constructor – ensures only one instance can be created (Singleton pattern).
     * Initializes lists and a new map.
     */
    protected GameWorld() {
        this.players = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.items = new ArrayList<>();
        this.map = new GameMap();
    }


    /**
     * Returns the singleton instance of the GameWorld.
     * Creates it if it doesn't exist yet.
     *
     * @return The single global GameWorld instance
     */
    public static GameWorld getInstance() {
        if (instance == null)
            instance = new GameWorld();
        return instance;
    }

    public void closeGame() {
        stopGame(); // Stop any ongoing game logic
        if (gameFrame != null) {
            gameFrame.dispose(); // Closes the game window
        }
    }


    public List<PlayerCharacter> getPlayers() { return players; }
    public List<Enemy> getEnemies() { return enemies; }
    public List<GameItem> getItems() { return items; }


    /**
     * Replaces the current game map with a new one.
     * Used during game setup.
     */
    public boolean setMap(GameMap map) {
        if (map != null) {
            this.map = map;
            return true;
        }
        return false;
    }

    public GameMap getMap() { return map; }

    public void setGameFrame(GameFrame frame) {
        this.gameFrame = frame;
    }

    public GameFrame getGameFrame() {
        return gameFrame;
    }





    public void startGame(){
        gameRunning.set(true);
    }

    public void stopGame() {
        gameRunning.set(false);

        // Stop enemy threads (assuming they have a stop method or use a flag)
        for (Enemy e : enemies) {
            if (e instanceof Runnable) {
                // You can implement a stop method or use a flag inside Enemy
                e.stopEnemy(); // Make sure this method exists in your Enemy class
            }
        }

        // Clear all entities
        for(PlayerCharacter p : players) {
            p.clearObservers();
        }
        players.clear();
        enemies.clear();
        items.clear();

        // Clear map (you might want to provide a reset/clear method in GameMap)
        if (map != null) {
            map.clear();
        }

    }


    public boolean isGameRunning() {
        return gameRunning.get();
    }

    public void addPlayer(PlayerCharacter player) {
        players.add(player);
    }

    public void removePlayer(PlayerCharacter player) {
        players.remove(player);
    }


    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
        Thread t = new Thread((Runnable) enemy); // start enemy in its own thread
        t.start();
    }

    public void removeEnemy(Enemy enemy) {
        enemies.remove(enemy);
    }

    /**
     * Fills the game map with entities (enemies, items, walls or empty space)
     * using random chance logic.
     *
     * @param size      The size of the game board.
     */
    public void prepareGame(int size) {
        //GameEntity entity;
        for (int row  = 0; row  < size; row ++) {
            for (int col  = 0; col  < size; col ++) {
                Position p = new Position(row, col );
                if(p.distanceTo(players.get(0).getPosition()) == 0) {continue;}

                GameEntity entity= getNewMapEntity(p);
                if (entity !=null){
                    entity.setPosition(p);
                    if(entity instanceof Enemy)
                    {
                        players.get(0).addObserver((Enemy)entity);
                        EXEC.schedule((Enemy)entity,0, TimeUnit.MILLISECONDS);
                    }
                }
                getMap().addEntity(p, entity);
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
    public GameEntity getNewMapEntity(Position pos) {
        Random rand = new Random();
        int chance = rand.nextInt(100); //

        if (chance < 40) {
            return null; //
        } else if (chance < 70) {
            int enemyType = rand.nextInt(3);
            switch (enemyType) {
                case 0 -> { return new Dragon(EXEC,gameRunning, BOARD_LOCK,50, pos); }
                case 1 -> { return new Orc(EXEC,gameRunning, BOARD_LOCK,50, pos); }
                case 2 -> { return new Goblin(EXEC,gameRunning, BOARD_LOCK,50, pos); }
                default -> { return null; }
            }
        } else if (chance < 80) {
            return new Wall(pos);
        } else {
            int potionChance = rand.nextInt(100); // 0-99
            if (potionChance < 75) {
                return new Potion(pos);
            } else {
                return new PowerPotion(pos);
            }
        }
    }

    public void setPlayer() {
        String name = JOptionPane.showInputDialog(null, "Enter your player name:", "Name", JOptionPane.QUESTION_MESSAGE);
        if (name == null || name.trim().isEmpty()) {
            name = "Player";
        }

        String[] options = {"Warrior", "Mage", "Archer"};
        int choice = JOptionPane.showOptionDialog(null,
                "Choose your character class:",
                "Character Selection",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);

        PlayerCharacter player = switch (choice) {
            case 0 -> new Warrior(name);
            case 1 -> new Mage(name);
            case 2 -> new Archer(name);
            default -> new Warrior(name); // default
        };

        GameMap map = getMap();
        Position pos;
        do {
            int row = new Random().nextInt(map.getMapSize());
            int col = new Random().nextInt(map.getMapSize());
            pos = new Position(row, col);
        } while (!map.getEntitiesAt(pos).isEmpty());

        player.setPosition(pos);
        player.setVisible(true);
        map.addEntity(pos, player);
        players.add(player);
    }


}
