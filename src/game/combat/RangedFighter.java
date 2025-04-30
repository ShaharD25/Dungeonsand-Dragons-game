//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.combat;
import game.map.Position;
/**
 * Interface for entities that can perform ranged (long-distance) attacks.
 * Implemented by characters like Archer and Dragon.
 */
public interface RangedFighter {

    /**
     * Performs a ranged attack on the target combatant.
     * The actual behavior is implemented by the specific class (e.g., Archer).
     *
     * @param target The combatant being attacked from range.
     */
    void fightRanged(Combatant target);

    /**
     * Returns the maximum attack range of the entity.
     * Determines how far the entity can attack.
     *
     * @return The range value (e.g., 2 or 3 tiles).
     */
    int getRange();

    /**
     * Checks if the target is within range based on current positions.
     * Usually compares the distance to the range returned by getRange().
     *
     * @param self   The attacker's current position.
     * @param target The target's current position.
     * @return true if target is within range, false otherwise.
     */
    boolean isInRange(Position self, Position target);
}
