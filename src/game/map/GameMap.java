package game.map;

import game.Main;
import game.audio.SoundPlayer;
import game.characters.Enemy;
import game.characters.PlayerCharacter;
import game.core.GameEntity;
import game.engine.GameWorld;
import game.gui.PopupPanel;
import game.items.Wall;
import game.logging.LogManager;

import javax.swing.*;
import java.util.*;

/**
 * Represents the 2D grid-based game board.
 * Stores a mapping between positions and the list of entities (items, players, enemies) at each cell.
 */
public class GameMap {
    private Map<Position, List<GameEntity>> grid = new HashMap<>();
    public int entitySize = 0;
    private int size;
    /**
     * Adds an entity to a specific position on the map.
     * If no entities exist at that position yet, a new list is created.
     *
     * @param pos    The position to place the entity at.
     * @param entity The entity to be added.
     */

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

    public void removeEntity(Position pos, GameEntity entity) {
        List<GameEntity> entities = grid.get(pos);
        if (entities != null && entity != null && entities.remove(entity)) {
            if (entities.isEmpty()) grid.remove(pos);
        }
    }


    /**
     * Returns the list of entities at a given position.
     * If no entities exist there, returns an empty list.
     *
     * @param pos The position to look at.
     * @return List of GameEntities (can be empty but never null).
     */
    public List<GameEntity> getEntitiesAt(Position pos) {
        return grid.computeIfAbsent(pos, k -> new ArrayList<>());
    }

    /**
     * Checks whether a position is valid (non-negative coordinates).
     *
     * @param pos The position to check.
     * @return true if the position is within valid board boundaries.
     */
    public boolean isValidPosition(Position pos) {
        return pos.getRow() >= 0 && pos.getCol() >= 0 && pos.getRow() < size && pos.getCol() < size;
    }

    /**
     * Returns the entire grid (used for visibility updates or debugging).
     *
     * @return A map of all positions and their entity lists.
     */
    public Map<Position, List<GameEntity>> getGrid() {
        return grid;
    }

    public int getEntityMapSize() {
        return (int) Math.sqrt(entitySize);
    }

    // Add this constructor to support new GameMap(size)
    public GameMap(int size) {
        this.size = size;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Position pos = new Position(row, col);
                grid.put(pos, new ArrayList<>());
            }
        }
    }

    public GameMap() {
        this(10);
    }

    public void clear() {
        grid.clear();
    }

    public boolean moveEntity(GameWorld world,Enemy enemy, Position start, Position nextStep) {
        GameMap map = world.getMap();
        int size = map.getMapSize();

        if (nextStep.getRow() < 0 || nextStep.getRow() >= size || nextStep.getCol() < 0 || nextStep.getCol() >= size) {
            PopupPanel.showPopup("Warning", "Cannot move outside the board!");
            return false;
        }

        List<GameEntity> entitiesAtNewPos = map.getEntitiesAt(nextStep);

        //should remove later if works
        boolean hasWall = entitiesAtNewPos.stream().anyMatch(e -> e instanceof Wall);

        if (hasWall) {
            SoundPlayer.playSound("hit.wav");
            PopupPanel.showPopup("Warning", "Blocked by wall!");
            return false;
        }

        // Remove player from the current cell
        map.removeEntity(start, enemy);

        // Set new position
        enemy.setPosition(nextStep);

        // Add player to the new cell
        map.addEntity(nextStep, enemy);

        world.getGameFrame().getMapPanel().updateMap();
        return true;
    }

    public int getMapSize(){return size;}

    public boolean isWall(Position pos)
    {
        List<GameEntity> entities = getEntitiesAt(pos);
        for(GameEntity e : entities)
        {
            if(e instanceof Wall)
                return true;
        }
        return false;
    }

    /**
     * Returns the Manhattan distance between two positions.
     */
    public static int calcDistance(Position p1, Position p2) {
        //return Math.abs(p1.getRow() - p2.getRow()) + Math.abs(p1.getCol() - p2.getCol());
        int dist = Math.abs(p1.getRow() - p2.getRow()) + Math.abs(p1.getCol() - p2.getCol());
        LogManager.log("Calculated distance between " + p1 + " and " + p2 + " = " + dist);
        return dist;
    }



    public List<GameEntity> getAllEntities() {
        List<GameEntity> all = new ArrayList<>();
        for (List<GameEntity> cellEntities : grid.values()) {
            all.addAll(cellEntities);
        }
        return all;
    }


    public static int getBoardSize() {
        int size = 10;
        while (true) {
            String input = JOptionPane.showInputDialog(null,
                    "Enter the board size (at least 10):", "Board Size", JOptionPane.QUESTION_MESSAGE);

            if (input == null) {
                JOptionPane.showMessageDialog(null, "No input provided. Defaulting to 10.");
                break;
            }

            try {
                size = Integer.parseInt(input);
                if (size >= 10) break;
                else JOptionPane.showMessageDialog(null, "Size must be at least 10.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        }
        return size;
    }
}
