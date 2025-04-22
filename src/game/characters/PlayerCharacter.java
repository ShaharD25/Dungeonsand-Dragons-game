package game.characters;
import game.core.Inventory;
import game.items.GameItem;
import game.items.Potion;
import game.items.PowerPotion;
import game.map.Position;

import java.util.Random;

/**
 * Abstract class representing a player-controlled character.
 * It extends AbstractCharacter and adds name, inventory, and treasure point tracking.
 */
public abstract class PlayerCharacter extends AbstractCharacter{
    private String name;
    private Inventory inventory;
    private int treasurePoints;

    /**
     * Constructs a new PlayerCharacter with a given name and random starting position on the board.
     */
    public PlayerCharacter(String name) {
        super(new Position(new Random().nextInt(10),new Random().nextInt(10)));
        this.name = name;
        this.inventory = new Inventory();
        this.treasurePoints = 0;
        this.setVisible(true);
    }


    /**
     * Returns the name of the player.
     */
    public String getName() {return name;}


    /**
     * Adds a GameItem to the player's inventory.
     * @param item the item to add
     * @return true (always succeeds for now)
     */
    public boolean addToInventory(GameItem item){
        inventory.addItem(item);
        return true;
    }

    /**
     * Uses the first regular Potion in the inventory (if any).
     * Removes it after use.
     * @return true if a potion was used, false otherwise
     */
    public boolean usePotion() {
        for (GameItem item : inventory.getItems()) {
            if (item instanceof Potion && !(item instanceof PowerPotion)) {
                ((Potion)item).interact(this);
                inventory.removeItem(item);
                return true;
            }
        }
        return false;
    }

    /**
     * Uses the first PowerPotion in the inventory (if any).
     * Removes it after use.
     * @return true if a power potion was used, false otherwise
     */
    public boolean usePowerPotion(){
        for (GameItem item : inventory.getItems()) {
            if (item instanceof PowerPotion) {
                ((PowerPotion)item).interact(this);
                inventory.removeItem(item);
                return true;
            }
        }
        return false;
    }

    /**
     * Adds treasure points to the player.
     * @param amount the number of points to add
     * @return true (always succeeds)
     */
    public boolean updateTreasurePoint(int amount){
        this.treasurePoints += amount;
        return true;
    }

    /**
     * Gets the total number of treasure points the player has earned.
     */
    public int getTreasurePoints(){return treasurePoints;}

    public String getDisplaySymbol() {
        return name.substring(0, 1).toUpperCase();
    }


}
