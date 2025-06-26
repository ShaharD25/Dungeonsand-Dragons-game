//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.core;
import java.util.*;
import game.items.GameItem;
import game.items.Potion;
import game.items.PowerPotion;
import game.items.Treasure;
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
    
 // Add copy constructor
    public Inventory(Inventory other) {
        this.items = new ArrayList<>();
        if (other != null && other.items != null) {
            // Deep copy each item
            for (GameItem item : other.items) {
                if (item instanceof Potion) {
                    this.items.add(new Potion(item.getPosition()));
                } else if (item instanceof PowerPotion) {
                    this.items.add(new PowerPotion(item.getPosition()));
                } else if (item instanceof Treasure) {
                    Treasure t = (Treasure) item;
                    this.items.add(new Treasure(t.getPosition(), t.isVisible(), t.getValue()));
                }
                // Add other item types as needed
            }
        }
    }
    
    // Or add a clone method
    public Inventory deepCopy() {
        return new Inventory(this);
    }

}
