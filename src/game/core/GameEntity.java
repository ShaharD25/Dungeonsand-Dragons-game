package game.core;


import game.map.Position;
/**
 * Interface representing any object that can exist on the game board.
 * This includes players, enemies, potions, treasures, walls, etc.
 *
 * All game entities must provide:
 * - A position (get/set)
 * - Visibility logic (for fog-of-war)
 * - A display symbol (for map rendering)
 */
public interface GameEntity {
    /**
     * Returns the current position of this entity on the map.
     *
     * @return The Position of the entity
     */
    Position getPosition();
    /**
     * Sets a new position for the entity on the map.
     *
     * @param newPos The new position
     */
    void setPosition(Position newPos);
    /**
     * Returns the symbol that should be displayed on the map for this entity.
     * This helps distinguish between entity types (e.g., "P" for potion, "D" for dragon).
     *
     * @return A short string or character
     */
    String getDisplaySymbol();
    /**
     * Updates whether the entity is currently visible to the player.
     * This is typically used in fog-of-war or vision systems.
     *
     * @param visible true to make visible, false to hide
     */
    void setVisible(boolean visible);
    /**
     * Checks whether the entity is currently visible to the player.
     *
     * @return true if the entity is visible
     */
    boolean isVisible();
}
