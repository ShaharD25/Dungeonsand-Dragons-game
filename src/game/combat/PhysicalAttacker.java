//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.combat;
/**
 * Interface for entities that use physical attacks (e.g., Warrior, Archer, Orc).
 * Physical attackers deal direct damage through melee or ranged strikes.
 */
public interface PhysicalAttacker {

    /**
     * Performs a physical attack on the target combatant.
     * Damage may vary based on power and critical hit chance.
     *
     * @param target The combatant being attacked.
     */
    boolean attack(Combatant target);

    /**
     * Determines whether the attack is a critical hit.
     * Critical hits typically deal extra damage (e.g., double).
     *
     * @return true if critical, false otherwise.
     */
    boolean isCriticalHit();
}
