package game.memento;
import game.characters.*;
import game.core.GameEntity;
import game.items.GameItem;
import game.map.GameMap;

import java.util.List;
public class GameMemento {
    private final List<PlayerCharacter> players;
    private final List<GameItem> items;
    private final List<Enemy> enemies;
    private final GameMap gameMap;

    public GameMemento(List<PlayerCharacter> players,List<GameItem> items,List<Enemy> enemies,GameMap gameMap)
    {
        this.players =players;
        this.enemies =enemies;
        this.items =items;
        this.gameMap=gameMap;
    }
    public List<PlayerCharacter> getPlayers() { return players; }
    public List<Enemy> getEnemies() { return enemies; }
    public List<GameItem> getItems() { return items; }
    public GameMap getMap() { return gameMap; }

}
