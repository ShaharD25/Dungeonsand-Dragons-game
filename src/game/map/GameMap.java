package game.map;

import game.core.GameEntity;

import java.util.*;

/**
 * Represents the 2D grid-based game board.
 * Stores a mapping between positions and the list of entities (items, players, enemies) at each cell.
 */
public class GameMap {
    private Map<Position, List<GameEntity>> grid = new HashMap<>();
    public int entitySize = 0;

    /**
     * Adds an entity to a specific position on the map.
     * If no entities exist at that position yet, a new list is created.
     *
     * @param pos    The position to place the entity at.
     * @param entity The entity to be added.
     */
//    public void addEntity(Position pos, GameEntity entity) {
//        if (entity == null) return;
//        grid.computeIfAbsent(pos, k -> new ArrayList<>()).add(entity);
//    }
    public boolean addEntity(Position pos, GameEntity entity) {
        entitySize++;
        if (pos == null || entity == null) return false;
        grid.computeIfAbsent(pos, k -> new ArrayList<>()).add(entity);
        return true;
    }


    /**
     * Removes a specific entity from a position on the map.
     * If the list becomes empty after removal, the entry is removed from the map.
     *
     * @param pos    The position from which to remove the entity.
     * @param entity The entity to be removed.
     */
//    public void removeEntity(Position pos, GameEntity entity) {
//        List<GameEntity> entities = grid.get(pos);
//        if (entities != null) {
//            entities.remove(entity);
//            if (entities.isEmpty()) {
//                grid.remove(pos);
//            }
//        }
//    }
    public boolean removeEntity(Position pos, GameEntity entity) {
        List<GameEntity> entities = grid.get(pos);
        if (entities != null && entity != null && entities.remove(entity)) {
            if (entities.isEmpty()) grid.remove(pos);
            return true;
        }
        return false;
    }


    /**
     * Returns the list of entities at a given position.
     * If no entities exist there, returns an empty list.
     *
     * @param pos The position to look at.
     * @return List of GameEntities (can be empty but never null).
     */
    public List<GameEntity> getEntitiesAt(Position pos) {
        return grid.getOrDefault(pos, Collections.emptyList());
    }

    /**
     * Checks whether a position is valid (non-negative coordinates).
     *
     * @param pos The position to check.
     * @return true if the position is within valid board boundaries.
     */
    public boolean isValidPosition(Position pos) {
        return pos.getRow() >= 0 && pos.getCol() >= 0;
    }

    /**
     * Returns the entire grid (used for visibility updates or debugging).
     *
     * @return A map of all positions and their entity lists.
     */
    public Map<Position, List<GameEntity>> getGrid() {
        return grid;
    }

    public int getMapSize() {
        return (int) Math.sqrt(entitySize);
    }

    // Add this constructor to support new GameMap(size)
    public GameMap(int size) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Position pos = new Position(row, col);
                grid.put(pos, new ArrayList<>());
            }
        }
    }

    public GameMap() {
        this(10); // ברירת מחדל לגודל 10×10
    }
}
