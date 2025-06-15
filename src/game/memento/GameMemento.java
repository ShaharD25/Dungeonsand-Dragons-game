// GameMemento.java
package game.memento;

import game.map.Position;

import java.util.ArrayList;
import java.util.List;

public class GameMemento {

    private final List<CharacterSnapshot> playerSnapshots;
    private final List<CharacterSnapshot> enemySnapshots;
    private final List<ItemSnapshot> itemSnapshots;

    public GameMemento(List<CharacterSnapshot> players, List<CharacterSnapshot> enemies, List<ItemSnapshot> items) {
        this.playerSnapshots = new ArrayList<>(players);
        this.enemySnapshots = new ArrayList<>(enemies);
        this.itemSnapshots = new ArrayList<>(items);
    }

    public List<CharacterSnapshot> getPlayerSnapshots() {
        return playerSnapshots;
    }

    public List<CharacterSnapshot> getEnemySnapshots() {
        return enemySnapshots;
    }

    public List<ItemSnapshot> getItemSnapshots() {
        return itemSnapshots;
    }

    public static class CharacterSnapshot {
        public final String type;
        public final int health;
        public final Position position;

        public CharacterSnapshot(String type, int health, Position position) {
            this.type = type;
            this.health = health;
            this.position = position;
        }
    }

    public static class ItemSnapshot {
        public final String type;
        public final Position position;

        public ItemSnapshot(String type, Position position) {
            this.type = type;
            this.position = position;
        }
    }
}
