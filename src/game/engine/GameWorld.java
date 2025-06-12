//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.engine;
import game.builder.PlayerBuilder;
import game.characters.*;
import game.core.GameEntity;
import game.factory.EnemyFactory;
import game.gui.GameFrame;
import game.items.GameItem;
import game.items.Potion;
import game.items.PowerPotion;
import game.items.Wall;
import game.logging.LogManager;
import game.map.GameMap;
import game.map.Position;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import game.gui.GameObserver;
import game.memento.GameMemento;

import javax.swing.*;

import static game.builder.PlayerBuilder.showDialog;


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
    public static final AtomicBoolean gameRunning = new AtomicBoolean(true);
    public static ReentrantLock BOARD_LOCK = new ReentrantLock(true);
    public static ScheduledExecutorService EXEC = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private final BlockingQueue<EnemyAction> enemyActions = new LinkedBlockingQueue<>();

    private ExecutorService enemyThreadPool;
    private int maxEnemies;

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

    public static GameWorld getNewWorld() {
        instance = new GameWorld();
        BOARD_LOCK = new ReentrantLock(true);
        EXEC = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
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
    public List<String> getMissingEnemies() {
        Set<Class<?>> existingTypes = new HashSet<>();
        for (Enemy e : enemies) {
            existingTypes.add(e.getClass());
        }

        List<String> missingEnemies = new ArrayList<>();

        if (!existingTypes.contains(Orc.class)) missingEnemies.add("Orc");
        if (!existingTypes.contains(Goblin.class)) missingEnemies.add("Goblin");
        if (!existingTypes.contains(Dragon.class)) missingEnemies.add("Dragon");

        return missingEnemies;
    }

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


    private void startActionProcessor() {
        Thread processor = new Thread(() -> {
            while (isGameRunning()) {
                try {
                    EnemyAction action = enemyActions.take(); // מחכה לפעולה
                    if (action.getEnemy().isDead() || action.getPlayer().isDead()) {
                        continue; // skip action for dead characters
                    }
                    if (action.isFight()) {
                        action.getEnemy().fightPlayer(action.getPlayer());
                    } else {
                        action.getEnemy().moveToPlayer(action.getPlayer());
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        processor.start();
    }

    public void enqueueEnemyAction(EnemyAction action) {
        enemyActions.offer(action);
    }


    public void startGame(){
        gameRunning.set(true);
        startActionProcessor();
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

    public int getMaxEnemies() {
        return maxEnemies;
    }

//    public int getCurrentEnemyCount() {
//        return (int) map.getAllEntities().stream()
//                .filter(e -> e instanceof Enemy && !((Enemy) e).isDead())
//                .count();
//    }

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
        players.get(0).addObserver(enemy);
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
                LogManager.log("at position:" + "(" + row + "," + col + ")");
                if (entity !=null){
                    entity.setPosition(p);
//                    if(entity instanceof Enemy)
//                    {
//                        addEnemy((Enemy)entity);
//                        EXEC.schedule((Enemy)entity,0, TimeUnit.MILLISECONDS);
//                    }
                }
                getMap().addEntity(p, entity);
            }
        }
        EnemyPool.init(size, size);
        int amountOfEnemies = Math.max(1,Math.min(10, (int) (size * size * 0.03)));
        for (int i = 0; i<amountOfEnemies; i++) {
            Enemy enemy = EnemyFactory.createRandomEnemy();
            addEnemy(enemy);
            getMap().addEntity(enemy.getPosition(), enemy);
            EnemyPool.instance().scheduleEnemy(enemy);
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
        int chance = rand.nextInt(70); //

        if (chance < 40) {
            return null; //
        }
//        else if (chance < 70) {
//            int enemyType = rand.nextInt(3);
//            String[] enemiesStr = {"Dragon", "Orc", "Goblin"};
//            LogManager.log("an enemy was created: " + enemiesStr[enemyType]);
//            switch (enemyType) {
//                case 0 -> { return new Dragon(EXEC,gameRunning, BOARD_LOCK,50, pos); }
//                case 1 -> { return new Orc(EXEC,gameRunning, BOARD_LOCK,50, pos); }
//                case 2 -> { return new Goblin(EXEC,gameRunning, BOARD_LOCK,50, pos); }
//                default -> { return null; }
//            }
//
//        }
       else if (chance < 50) {
            LogManager.log("an wall was created");
            return new Wall(pos);
        } else {
            int potionChance = rand.nextInt(100); // 0-99
            if (potionChance < 75) {
                LogManager.log("an life potion was created");
                return new Potion(pos);
            } else {
                LogManager.log("an power potion was created");
                return new PowerPotion(pos);
            }
        }
    }

    public void setPlayer() {
        PlayerCharacter player = showDialog();
        Position pos = getFreeRandomPosition();
        LogManager.log("the chosen character is located at: " + "("+ pos.getRow() + "," + pos.getCol() +")");
        player.setPosition(pos);
        player.setVisible(true);
        map.addEntity(pos, player);
        players.add(player);
    }

    public void loadState(GameMemento memento) {
        this.players = memento.getPlayers();
        this.enemies = memento.getEnemies();
        this.items = memento.getItems();
        this.map = memento.getMap();
    }

    public Position getFreeRandomPosition() {
        GameMap map = getMap();
        Position pos;
        do {
            int row = new Random().nextInt(map.getMapSize());
            int col = new Random().nextInt(map.getMapSize());
            pos = new Position(row, col);
        } while (!map.getEntitiesAt(pos).isEmpty());
        return pos;
    }

//    public GameMemento saveState() {
//        List<PlayerCharacter> playerCopies = new ArrayList<>(players);
//        List<Enemy> enemyCopies = new ArrayList<>(enemies);
//        List<GameItem> itemCopies = new ArrayList<>(items);
//        GameMap mapCopy = map.copy(); //
//
//        return new GameMemento(playerCopies, enemyCopies, itemCopies, mapCopy);
//    }


}
