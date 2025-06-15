//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.items;

import game.core.GameEntity;
import game.map.Position;

/**
 * Abstract base class for all items that can appear on the game board.
 * Items can include potions, treasures, walls, etc.
 * This class implements GameEntity and provides position, visibility, blocking, and display information.
 */
public abstract class GameItem implements GameEntity {
    private Position position;
    private boolean blocksMovement;
    private String description;
    private boolean visible;
    private String displaySymbol;

    // Added field for type
    protected String type;

    /**
     * Constructs a GameItem with full customization.
     *
     * @param position        The item's position on the board
     * @param blocksMovement  Whether this item blocks movement
     * @param description     Name or description of the item
     * @param visible         Whether the item is initially visible
     * @param displaySymbol   Symbol to display for the item
     */
    public GameItem(Position position, boolean blocksMovement, String description, boolean visible, String displaySymbol) {
        this.position = position;
        this.blocksMovement = blocksMovement;
        this.description = description;
        this.visible = visible;
        this.displaySymbol = displaySymbol;
        this.type = this.getClass().getSimpleName(); // Set type based on class name
    }

    public Position getPosition() {
        return position;
    }

    public boolean setPosition(Position position) {
        if (position != null) {
            this.position = position;
            return true;
        }
        return false;
    }

    public boolean isBlocksMovement() {
        return blocksMovement;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplaySymbol() {
        return displaySymbol;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    public boolean setDescription(String description) {
        if (description != null && !description.isBlank()) {
            this.description = description;
            return true;
        }
        return false;
    }

    // Added getType() to support memento snapshot reconstruction
    public String getType() {
        return this.type;
    }
}
