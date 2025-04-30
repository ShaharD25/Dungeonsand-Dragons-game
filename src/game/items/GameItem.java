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

    /**
     * Constructs a GameItem with full customization.
     *
     * @param position        The item's position on the board
     * @param blocksMovement  Whether this item blocks movement
     * @param description     Name or description of the item
     * @param visible         Whether the item is initially visible
     * @param displaySymbol   Symbol to display for the item (e.g., "P" for potion)
     */
    public GameItem(Position position, boolean blocksMovement, String description, boolean visible, String displaySymbol) {
        this.position = position;
        this.blocksMovement = blocksMovement;
        this.description = description;
        this.visible = visible;
        this.displaySymbol = displaySymbol;
    }

    /**
     * Returns the current position of the item.
     */
    public Position getPosition(){return position;}

//    /**
//     * Updates the position of the item.
//     *
//     * @param newPos New position for the item
//     */
//    public void setPosition(Position newPos){this.position = newPos;}

    public boolean setPosition(Position position) {
        if (position != null) {
            this.position = position;
            return true;
        }
        return false;
    }

    /**
     * Indicates whether this item blocks movement on the board.
     * @return true if movement is blocked
     */
    public boolean isBlocksMovement(){return blocksMovement;}

    /**
     * Returns the description or name of the item.
     */
    public String getDescription(){return description;}

    /**
     * Sets a new description for the item.
     */
    public String getDisplaySymbol(){return displaySymbol;}

    /**
     * Updates the visibility of the item.
     */
    public void setVisible(boolean visible){this.visible = visible;}

    /**
     * Sets a new description for the item.
     */
    //public void setDescription(String description){this.description = description;}

    public boolean setDescription(String description) {
        if (description != null && !description.isBlank()) {
            this.description = description;
            return true;
        }
        return false;
    }
}
