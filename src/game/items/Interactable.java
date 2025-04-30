//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.items;
import game.characters.PlayerCharacter;

/**
 * Interface for any game object that can be interacted with by the player.
 * Typically used by items like potions or treasures.
 */
public interface Interactable {
    /**
     * Defines the behavior when a player interacts with this item.
     *
     * @param c The player character interacting with the item.
     */
    void interact(PlayerCharacter c);
}
