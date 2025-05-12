//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.engine;
import game.characters.Enemy;
import game.characters.PlayerCharacter;
import game.core.GameEntity;
import game.gui.GameFrame;
import game.items.GameItem;
import game.map.GameMap;
import game.map.Position;

import java.util.ArrayList;
import java.util.List;
import game.gui.GameObserver;


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
    private final List<GameObserver> observers = new ArrayList<>();
    private GameFrame gameFrame;



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

    //public void addPlayer(PlayerCharacter player) {players.add(player);}
    //public void removePlayer(PlayerCharacter player) {players.remove(player);}
    public List<PlayerCharacter> getPlayers() { return players; }
    public List<Enemy> getEnemies() { return enemies; }
    public List<GameItem> getItems() { return items; }
    //public void addItem(GameItem item) {items.add(item); }


    //public void addEnemy(Enemy enemy){enemies.add(enemy);}
    //public void removeEnemy(Enemy enemy){enemies.remove(enemy);}

    /**
     * Replaces the current game map with a new one.
     * Used during game setup.
     */
    //public void setMap(GameMap map) {this.map = map;}
    public boolean setMap(GameMap map) {
        if (map != null) {
            this.map = map;
            return true;
        }
        return false;
    }

    public GameMap getMap() { return map; }


    //public void addEntityToMap(Position pos, GameEntity entity) {map.addEntity(pos, entity);}
    //public void removeEntityFromMap(Position pos, GameEntity entity) { map.removeEntity(pos, entity);}
    //public List<GameEntity> getEntitiesAt(Position pos) { return map.getEntitiesAt(pos);}
    //public boolean isValidPosition(Position pos) {return map.isValidPosition(pos);  }

    public void setGameFrame(GameFrame frame) {
        this.gameFrame = frame;
    }

    public GameFrame getGameFrame() {
        return gameFrame;
    }

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers() {
        for (GameObserver observer : observers) {
       }
    }

}
