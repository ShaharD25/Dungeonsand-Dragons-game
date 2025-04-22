package game.core;
import java.util.*;
import game.items.GameItem;
import game.items.Potion;
import game.items.PowerPotion;

/**
 * Represents the player's inventory, which holds items collected throughout the game.
 * Items can be added, removed, and retrieved.
 */
public class Inventory {
    private List<GameItem> items ;
    public Inventory() {
        this.items = new ArrayList<>();
    }

    /**
     * Adds an item to the inventory.
     *
     * @param item The item to add
     */
    public void addItem(GameItem item) {
        if (item != null) {
            items.add(item);
        }
    }

    /**
     * Removes a specific item from the inventory.
     *
     * @param item The item to remove
     */
    public void removeItem(GameItem item){
        items.remove(item);
    }

    /**
     * Returns the full list of items currently in the inventory.
     *
     * @return A list of GameItem objects
     */
    public List<GameItem> getItems(){return items;}

}
