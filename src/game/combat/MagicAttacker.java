//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.combat;
/**
 * Interface for entities that can perform magical attacks.
 * Used by characters like Mage or Dragon who have elemental abilities.
 */
public interface MagicAttacker {

    /**
     * Calculates and applies the magical damage against a target.
     * Often considers elemental strengths and damage scaling.
     *
     * @param combatant The target to apply the calculated magic damage to.
     */
    void calculateMagicDamage(Combatant combatant);

    /**
     * Performs a magical attack (spell) on the target combatant.
     * May involve elemental damage and special logic based on magic type.
     *
     * @param target The target to cast the spell on
     */
    void castSpell(Combatant target);

    /**
     * Returns the elemental type of the magic (e.g., FIRE, ICE, LIGHTNING, ACID).
     * Used for determining elemental strengths and weaknesses.
     *
     * @return The magic element of this attacker
     */
    MagicElement getElement();

    /**
     * Compares the elemental affinity of this attacker with another.
     * Determines if this attacker's element is stronger than the other.
     *
     * @param other The other magic attacker to compare against
     * @return true if this attacker’s element is stronger
     */
    boolean isElementStrongerThan(MagicAttacker other);
}
