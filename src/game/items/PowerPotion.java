//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.items;
import game.characters.PlayerCharacter;
import game.items.Potion;
import game.map.Position;

import java.util.Random;
/**
 * Represents a power-enhancing potion.
 * When used, it increases the player's attack power.
 */
public class PowerPotion extends Potion{
    public PowerPotion(Position position) {
        super(position);
        this.setDescription("Power Potion");
        setIncreaseAmount(new Random().nextInt(5) + 1);
    }

    /**
     * When the player interacts with this potion, their power increases.
     * Overrides the behavior of a regular Potion.
     *
     * @param c The player who uses the power potion
     */
    @Override
    public void interact(PlayerCharacter c) {
        int currentPower = c.getPower();
        currentPower += getIncreaseAmount();
        c.setPower(currentPower);
        setUsed(true);
    }
}
