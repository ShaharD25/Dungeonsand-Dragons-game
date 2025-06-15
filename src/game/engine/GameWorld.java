//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.engine;

import game.builder.PlayerBuilder;
import game.characters.*;
import game.core.GameEntity;
import game.factory.EnemyFactory;
import game.gui.GameFrame;
import game.items.*;
import game.logging.LogManager;
import game.map.GameMap;
import game.map.Position;
import game.memento.GameCaretaker;
import game.memento.GameMemento;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static game.builder.PlayerBuilder.showDialog;

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

    // Caretaker for Memento
    private final GameCaretaker caretaker = new GameCaretaker();

    public GameCaretaker getCaretaker() {
        return caretaker;
    }

    protected GameWorld() {
        this.players = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.items = new ArrayList<>();
        this.map = new GameMap();
    }

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
        stopGame();
        if (gameFrame != null) {
            gameFrame.dispose();
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

    public GameFrame getGameFrame() { return gameFrame; }

    private void startActionProcessor() {
        Thread processor = new Thread(() -> {
            while (isGameRunning()) {
                try {
                    EnemyAction action = enemyActions.take();
                    if (action.getEnemy().isDead() || action.getPlayer().isDead()) {
                        continue;
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
        for (Enemy e : enemies) {
            if (e instanceof Runnable) {
                e.stopEnemy();
            }
        }
        for(PlayerCharacter p : players) {
            p.clearObservers();
        }
        players.clear();
        enemies.clear();
        items.clear();
        if (map != null) {
            map.clear();
        }
    }

    public int getMaxEnemies() {
        return maxEnemies;
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
        players.get(0).addObserver(enemy);
    }

    public void removeEnemy(Enemy enemy) {
        enemies.remove(enemy);
    }

    public void prepareGame(int size) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Position p = new Position(row, col);
                if (p.distanceTo(players.get(0).getPosition()) == 0) continue;
                GameEntity entity = getNewMapEntity(p);
                LogManager.log("at position:" + "(" + row + "," + col + ")");
                if (entity != null) {
                    entity.setPosition(p);
                }
                getMap().addEntity(p, entity);
            }
        }
        EnemyPool.init(size, size);
        int amountOfEnemies = Math.max(1, Math.min(10, (int) (size * size * 0.03)));
        for (int i = 0; i < amountOfEnemies; i++) {
            Enemy enemy = EnemyFactory.createRandomEnemy();
            addEnemy(enemy);
            getMap().addEntity(enemy.getPosition(), enemy);
            EnemyPool.instance().scheduleEnemy(enemy);
        }
    }

    public GameEntity getNewMapEntity(Position pos) {
        Random rand = new Random();
        int chance = rand.nextInt(70);
        if (chance < 40) return null;
        else if (chance < 50) return new Wall(pos);
        else {
            int potionChance = rand.nextInt(100);
            return (potionChance < 75) ? new Potion(pos) : new PowerPotion(pos);
        }
    }

    public void setPlayer() {
        PlayerCharacter player = showDialog();
        Position pos = getFreeRandomPosition();
        player.setPosition(pos);
        player.setVisible(true);
        map.addEntity(pos, player);
        players.add(player);
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

    public void addItem(GameItem item) {
        items.add(item);
    }

    // saveState for Memento
    public GameMemento saveState() {
        List<GameMemento.CharacterSnapshot> playerSnapshots = new ArrayList<>();
        for (PlayerCharacter p : players) {
            playerSnapshots.add(new GameMemento.CharacterSnapshot(
                    p.getClass().getSimpleName(), p.getHealth(), p.getPosition()
            ));
        }

        List<GameMemento.CharacterSnapshot> enemySnapshots = new ArrayList<>();
        for (Enemy e : enemies) {
            enemySnapshots.add(new GameMemento.CharacterSnapshot(
                    e.getClass().getSimpleName(), e.getHealth(), e.getPosition()
            ));
        }

        List<GameMemento.ItemSnapshot> itemSnapshots = new ArrayList<>();
        for (GameItem item : items) {
            itemSnapshots.add(new GameMemento.ItemSnapshot(
                    item.getType(), item.getPosition()
            ));
        }

        return new GameMemento(playerSnapshots, enemySnapshots, itemSnapshots);
    }

    public void restoreFromMemento(GameMemento memento) {
        stopGame(); // Clears game entities and map
        map.clear(); // Make sure map is also cleared

        // Restore players
        for (GameMemento.CharacterSnapshot snap : memento.getPlayerSnapshots()) {
            PlayerCharacter player = switch (snap.type) {
                case "Mage" -> new Mage("RestoredMage");
                case "Warrior" -> new Warrior("RestoredWarrior");
                case "Archer" -> new Archer("RestoredArcher");
                default -> null;
            };
            if (player != null) {
                player.setHealth(snap.health);
                player.setPosition(snap.position);
                player.setVisible(true);
                addPlayer(player);
                map.addEntity(snap.position, player);
            }
        }

        // Restore enemies
        for (GameMemento.CharacterSnapshot snap : memento.getEnemySnapshots()) {
            Enemy enemy = switch (snap.type) {
                case "Goblin" -> new Goblin(EXEC, gameRunning, BOARD_LOCK, snap.health, snap.position);
                case "Orc" -> new Orc(EXEC, gameRunning, BOARD_LOCK, snap.health, snap.position);
                case "Dragon" -> new Dragon(EXEC, gameRunning, BOARD_LOCK, snap.health, snap.position);
                default -> null;
            };
            if (enemy != null) {
                addEnemy(enemy);
                map.addEntity(snap.position, enemy);
                players.get(0).addObserver(enemy);
                EnemyPool.instance().scheduleEnemy(enemy); // Start moving
            }
        }

        // Restore items
        for (GameMemento.ItemSnapshot snap : memento.getItemSnapshots()) {
            GameItem item = switch (snap.type) {
                case "Potion" -> new Potion(snap.position);
                case "PowerPotion" -> new PowerPotion(snap.position);
                case "Treasure" -> new Treasure(snap.position, true, 100);
                case "Wall" -> new Wall(snap.position);
                default -> null;
            };
            if (item != null) {
                addItem(item);
                map.addEntity(snap.position, item);
            }
        }


        startGame();

        if (gameFrame != null) {
            gameFrame.getMapPanel().updateMap();
            gameFrame.getStatusPanel().updateStatus(players.get(0));

        }
    }

}

