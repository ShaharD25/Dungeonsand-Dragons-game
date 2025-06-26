//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.engine;

import game.builder.PlayerBuilder;
import game.characters.*;
import game.core.GameEntity;
import game.decorators.BoostedAttackDecorator;
import game.decorators.EnemyDecorator;
import game.decorators.ExplodingEnemyDecorator;
import game.decorators.PlayerDecorator;
import game.decorators.RegenerationDecorator;
import game.decorators.ShieldedPlayerDecorator;
import game.decorators.TeleportingEnemyDecorator;
import game.decorators.VampireEnemyDecorator;
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

//    private void startActionProcessor() {
//        Thread processor = new Thread(() -> {
//            while (isGameRunning()) {
//                try {
//                    EnemyAction action = enemyActions.take();
//                    if (action.getEnemy().isDead() || action.getPlayer().isDead()) {
//                        continue;
//                    }
//                    if (action.isFight()) {
//                        action.getEnemy().fightPlayer(action.getPlayer());
//                    } else {
//                        action.getEnemy().moveToPlayer(action.getPlayer());
//                    }
//                } catch (InterruptedException e) {
//                    break;
//                }
//            }
//        });
//        processor.start();
//    }

    public void enqueueEnemyAction(EnemyAction action) {
        enemyActions.offer(action);
    }

    public void startGame(){
        gameRunning.set(true);
        //startActionProcessor();
        startDecoratorTimer();
    }

    public void startDecoratorTimer() {
        Thread decoratorTimer = new Thread(() -> {
            while (isGameRunning()) {
                try {
                    List<Enemy> snapshot = new ArrayList<>(getEnemies());   // צילום מצב – אין שינוי תוך כדי לולאה
                    List<Enemy> toUpgrade = new ArrayList<>();

                    for (Enemy e : snapshot) {                // אוספים את המועמדים
                        if (!(e instanceof EnemyDecorator) && !e.isDead()) {
                            if (Math.random() < 0.2) {       // 20 % סיכוי
                                toUpgrade.add(e);
                            }
                        }
                    }

                    for (Enemy e : toUpgrade) {               // מבצעים את ההחלפה בפועל
                        Enemy decorated = EnemyFactory.wrapWithRandomDecorator(e);

                        // שימור מאפיינים חשובים
                        decorated.setVisible(e.isVisible());

                        getMap().replaceEntity(e, decorated);
                        removeEnemy(e);
                        addEnemy(decorated);

                        // *** חשוב – שולחים את ה-Decorator ל-Scheduler ***
                        EnemyPool.instance().scheduleEnemy(decorated);

                        System.out.println("[DEBUG] Enemy upgraded to: " + decorated);
                    }

                    Thread.sleep(3000);                       // סריקה כל 3 שניות
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        decoratorTimer.start();
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
            //getMap().addEntity(enemy.getPosition(), enemy);
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
        
        // Save players with decorator information
        for (PlayerCharacter p : players) {
            List<GameMemento.DecoratorInfo> decoratorChain = new ArrayList<>();
            PlayerCharacter current = p;
            String baseType = null;
            
            // Traverse decorator chain to find base type and collect decorator info
            while (current != null) {
                if (current instanceof PlayerDecorator) {
                    // Save decorator-specific state
                    Map<String, Object> state = new HashMap<>();
                    
                    if (current instanceof ShieldedPlayerDecorator) {
                        ShieldedPlayerDecorator shield = (ShieldedPlayerDecorator) current;
                        state.put("shieldAvailable", shield.isShieldActive());
                    } else if (current instanceof BoostedAttackDecorator) {
                        BoostedAttackDecorator boost = (BoostedAttackDecorator) current;
                        state.put("powerBoost", boost.getPowerBoost());
                    } else if (current instanceof RegenerationDecorator) {
                        // RegenerationDecorator doesn't have special state beyond being active
                    }
                    
                    decoratorChain.add(new GameMemento.DecoratorInfo(
                        current.getClass().getSimpleName(), state
                    ));
                    
                    current = ((PlayerDecorator) current).getWrapped();
                } else {
                    // Found base player type
                    baseType = current.getClass().getSimpleName();
                    break;
                }
            }
            
            playerSnapshots.add(new GameMemento.CharacterSnapshot(
                baseType, 
                p.getHealth(), 
                p.getPower(),
                p.getPosition(),
                p.isVisible(),
                p.getName(),
                p.getInventory(),
                p.getTreasurePoints(),
                decoratorChain
            ));
        }

        List<GameMemento.CharacterSnapshot> enemySnapshots = new ArrayList<>();
        
        // Save enemies with decorator information
        for (Enemy e : enemies) {
            List<GameMemento.DecoratorInfo> decoratorChain = new ArrayList<>();
            Enemy current = e;
            String baseType = null;
            int baseLoot = 0;
            
            // Traverse decorator chain
            while (current != null) {
                if (current instanceof EnemyDecorator) {
                    Map<String, Object> state = new HashMap<>();
                    
                    if (current instanceof TeleportingEnemyDecorator) {
                    		TeleportingEnemyDecorator teleporter = (TeleportingEnemyDecorator) current;
                        state.put("hasTeleported", teleporter.hasTeleported());
                    } else if (current instanceof ExplodingEnemyDecorator) {
                        // No special state for exploding decorator
                    		state.put("active", true);
                    } else if (current instanceof VampireEnemyDecorator) {
                        // No special state for vampire decorator
                    		state.put("active", true);
                    }
                    
                    decoratorChain.add(new GameMemento.DecoratorInfo(
                        current.getClass().getSimpleName(), state
                    ));
                    
                    current = ((EnemyDecorator) current).getWrapped();
                } else {
                    // Found base enemy type
                    baseType = current.getClass().getSimpleName();
                    baseLoot = current.getLoot();
                    break;
                }
            }
            
            enemySnapshots.add(new GameMemento.CharacterSnapshot(
                baseType,
                e.getHealth(),
                e.getPower(),
                e.getPosition(),
                e.isVisible(),
                baseLoot,
                decoratorChain
            ));
        }

        List<GameMemento.ItemSnapshot> itemSnapshots = new ArrayList<>();
        
        // Save all items on the map (not just those in the items list)
        for (Map.Entry<Position, List<GameEntity>> entry : map.getGrid().entrySet()) {
            for (GameEntity entity : entry.getValue()) {
                if (entity instanceof GameItem item) {
                    int value = 0;
                    if (item instanceof Treasure) {
                        value = ((Treasure) item).getValue();
                    }
                    itemSnapshots.add(new GameMemento.ItemSnapshot(
                        item.getType(), 
                        item.getPosition(),
                        value
                    ));
                }
            }
        }

        return new GameMemento(playerSnapshots, enemySnapshots, itemSnapshots);
    }
    
    
//    public GameMemento saveState() {
//        List<GameMemento.CharacterSnapshot> playerSnapshots = new ArrayList<>();
//        for (PlayerCharacter p : players) {
//            playerSnapshots.add(new GameMemento.CharacterSnapshot(
//                    p.getClass().getSimpleName(), p.getHealth(), p.getPosition(),p.getName(),p.getInventory(),p.getTreasurePoints()
//            ));
//        }
//
//        List<GameMemento.CharacterSnapshot> enemySnapshots = new ArrayList<>();
//        for (Enemy e : enemies) {
//            enemySnapshots.add(new GameMemento.CharacterSnapshot(
//                    e.getClass().getSimpleName(), e.getHealth(), e.getPosition()
//            ));
//        }
//
//        List<GameMemento.ItemSnapshot> itemSnapshots = new ArrayList<>();
//        for (GameItem item : items) {
//            itemSnapshots.add(new GameMemento.ItemSnapshot(
//                    item.getType(), item.getPosition()
//            ));
//        }
//
//        return new GameMemento(playerSnapshots, enemySnapshots, itemSnapshots);
//    }

    
    public void restoreFromMemento(GameMemento memento) {
        stopGame(); // Clears game entities and map
        map.clear(); // Make sure map is also cleared
        
        // Need to reinitialize since stopGame might have shut things down
        BOARD_LOCK = new ReentrantLock(true);
        EXEC = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        gameRunning.set(true);
        EnemyPool.init(map.getMapSize(), map.getMapSize());
        
        // Restore players
        for (GameMemento.CharacterSnapshot snap : memento.getPlayerSnapshots()) {
            // Create base player
            PlayerCharacter player = switch (snap.baseType) {
                case "Mage" -> new Mage(snap.name != null ? snap.name : "RestoredMage");
                case "Warrior" -> new Warrior(snap.name != null ? snap.name : "RestoredWarrior");
                case "Archer" -> new Archer(snap.name != null ? snap.name : "RestoredArcher");
                default -> null;
            };
            
            if (player != null) {
                // Set base attributes
                player.setHealth(snap.health);
                player.setPower(snap.power);
                player.setPosition(snap.position);
                player.setVisible(snap.isVisible);
                
                // Restore inventory and treasure points
                if (snap.inventory != null) {
                    // Copy inventory items
                    for (GameItem item : snap.inventory.getItems()) {
                        player.addToInventory(item);
                    }
                }
                player.updateTreasurePoint(snap.treasurePoints);
                
                // Apply decorators in reverse order (innermost to outermost)
                for (int i = snap.decorators.size() - 1; i >= 0; i--) {
                    GameMemento.DecoratorInfo decInfo = snap.decorators.get(i);
                    player = applyPlayerDecorator(player, decInfo);
                }
                
                addPlayer(player);
                map.addEntity(snap.position, player);
            }
        }

        // Restore enemies
        for (GameMemento.CharacterSnapshot snap : memento.getEnemySnapshots()) {
            // Create base enemy
            Enemy enemy = switch (snap.baseType) {
                case "Goblin" -> new Goblin(EXEC, gameRunning, BOARD_LOCK, snap.loot, snap.position);
                case "Orc" -> new Orc(EXEC, gameRunning, BOARD_LOCK, snap.loot, snap.position);
                case "Dragon" -> new Dragon(EXEC, gameRunning, BOARD_LOCK, snap.loot, snap.position);
                default -> null;
            };
            
            if (enemy != null) {
                // Set base attributes
                enemy.setHealth(snap.health);
                enemy.setPower(snap.power);
                enemy.setVisible(snap.isVisible);
                
                // Apply decorators in reverse order
                for (int i = snap.decorators.size() - 1; i >= 0; i--) {
                    GameMemento.DecoratorInfo decInfo = snap.decorators.get(i);
                    enemy = applyEnemyDecorator(enemy, decInfo);
                }
                
                addEnemy(enemy);
                map.addEntity(snap.position, enemy);
                
                if (!players.isEmpty()) {
                    players.get(0).addObserver(enemy);
                }
                
                EnemyPool.instance().scheduleEnemy(enemy);
            }
        }

        // Restore items
        for (GameMemento.ItemSnapshot snap : memento.getItemSnapshots()) {
            GameItem item = switch (snap.type) {
                case "Potion" -> new Potion(snap.position);
                case "PowerPotion" -> new PowerPotion(snap.position);
                case "Treasure" -> new Treasure(snap.position, true, snap.value);
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
            if (!players.isEmpty()) {
                gameFrame.getStatusPanel().updateStatus(players.get(0));
            }
        }
    }

    // Helper method to apply player decorators
    private PlayerCharacter applyPlayerDecorator(PlayerCharacter player, GameMemento.DecoratorInfo decInfo) {
        switch (decInfo.decoratorType) {
            case "ShieldedPlayerDecorator":
                ShieldedPlayerDecorator shield = new ShieldedPlayerDecorator(player);
                // Restore shield state
                Boolean shieldAvailable = (Boolean) decInfo.state.get("shieldAvailable");
                if (shieldAvailable != null && !shieldAvailable) {
                    shield.consumeShield();
                }
                return shield;
                
            case "BoostedAttackDecorator":
                Integer powerBoost = (Integer) decInfo.state.get("powerBoost");
                return new BoostedAttackDecorator(player, powerBoost != null ? powerBoost : 10);
                
            case "RegenerationDecorator":
                return new RegenerationDecorator(player);
                
            default:
                return player;
        }
    }

    // Helper method to apply enemy decorators
    private Enemy applyEnemyDecorator(Enemy enemy, GameMemento.DecoratorInfo decInfo) {
        switch (decInfo.decoratorType) {
            case "TeleportingEnemyDecorator":
                TeleportingEnemyDecorator teleporter = new TeleportingEnemyDecorator(enemy);
                // Restore teleport state
                Boolean hasTeleported = (Boolean) decInfo.state.get("hasTeleported");
                if (hasTeleported != null && hasTeleported) {
                    teleporter.setHasTeleported(true);
                }
                return teleporter;
                
            case "ExplodingEnemyDecorator":
                return new ExplodingEnemyDecorator(enemy);
                
            case "VampireEnemyDecorator":
                return new VampireEnemyDecorator(enemy);
                
            default:
                return enemy;
        }
    }
    
    
//    public void restoreFromMemento(GameMemento memento) {
//        stopGame(); // Clears game entities and map
//        map.clear(); // Make sure map is also cleared
//
//        // Restore players
//        for (GameMemento.CharacterSnapshot snap : memento.getPlayerSnapshots()) {
//            PlayerCharacter player = switch (snap.type) {
//                case "Mage" -> new Mage("RestoredMage");
//                case "Warrior" -> new Warrior("RestoredWarrior");
//                case "Archer" -> new Archer("RestoredArcher");
//                default -> null;
//            };
//            if (player != null) {
//                player.setHealth(snap.health);
//                player.setPosition(snap.position);
//                player.setVisible(true);
//                addPlayer(player);
//                map.addEntity(snap.position, player);
//            }
//        }
//
//        // Restore enemies
//        for (GameMemento.CharacterSnapshot snap : memento.getEnemySnapshots()) {
//            Enemy enemy = switch (snap.type) {
//                case "Goblin" -> new Goblin(EXEC, gameRunning, BOARD_LOCK, snap.health, snap.position);
//                case "Orc" -> new Orc(EXEC, gameRunning, BOARD_LOCK, snap.health, snap.position);
//                case "Dragon" -> new Dragon(EXEC, gameRunning, BOARD_LOCK, snap.health, snap.position);
//                default -> null;
//            };
//            if (enemy != null) {
//                addEnemy(enemy);
//                map.addEntity(snap.position, enemy);
//                players.get(0).addObserver(enemy);
//                EnemyPool.instance().scheduleEnemy(enemy); // Start moving
//            }
//        }
//
//        // Restore items
//        for (GameMemento.ItemSnapshot snap : memento.getItemSnapshots()) {
//            GameItem item = switch (snap.type) {
//                case "Potion" -> new Potion(snap.position);
//                case "PowerPotion" -> new PowerPotion(snap.position);
//                case "Treasure" -> new Treasure(snap.position, true, 100);
//                case "Wall" -> new Wall(snap.position);
//                default -> null;
//            };
//            if (item != null) {
//                addItem(item);
//                map.addEntity(snap.position, item);
//            }
//        }
//
//
//        startGame();
//
//        if (gameFrame != null) {
//            gameFrame.getMapPanel().updateMap();
//            gameFrame.getStatusPanel().updateStatus(players.get(0));
//
//        }
//    }

}

