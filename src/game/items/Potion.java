//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.items;
import java.util.Random;

import game.characters.PlayerCharacter;
import game.map.Position;
/**
 * Represents a basic healing potion.
 * When used, it restores a fixed amount of health to the player.
 */
public class Potion extends GameItem implements Interactable {
    private int increaseAmount;
    private boolean isUsed;

    /**
     * Constructs a healing potion and places it at a given position.
     *
     * @param position The position where the potion is placed on the board.
     */
    public Potion(Position position) {
        super(position, true, "Potion",false,"P");
        this.increaseAmount = new Random().nextInt(41) + 10;
        this.isUsed = false;
    }


    public boolean getIsUsed() {
        return isUsed;
    }
    public int getIncreaseAmount() {
        return increaseAmount;
    }

    public void setIncreaseAmount(int increaseAmount) { this.increaseAmount = increaseAmount; }
    public void setUsed(boolean isUsed) { this.isUsed = isUsed; }

    /**
     * When the player interacts with this potion, they are healed.
     *
     * @param c The player who uses the potion.
     */
    @Override
    public void interact(PlayerCharacter c) {
        int currentHealth = c.getHealth();
        currentHealth += increaseAmount;
        c.setHealth(currentHealth);
        this.isUsed = true;
    }
}
