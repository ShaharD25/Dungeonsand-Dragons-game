package game.combat;

import game.core.GameEntity;
/**
 * Interface for any entity that can engage in combat.
 * This includes players, enemies, and any other character that can take or deal damage.
 * Extends GameEntity, so every combatant must also have position and visibility.
 */
public interface Combatant extends GameEntity {

    /**
     * Gets the current health of the combatant.
     *
     * @return Current HP (usually between 0 and 100)
     */
    int getHealth();

    /**
     * Sets the current health of the combatant.
     * Typically clamped between 0 and 100 by the implementing class.
     *
     * @param health New health value to assign
     */
    void setHealth(int health);

    /**
     * Called when the combatant receives damage from another source.
     * The logic here may include evasion checks, resistances, or critical hit handling.
     *
     * @param amount The amount of damage to be taken
     * @param source The attacker who caused the damage
     */
    void receiveDamage(int amount, Combatant source);

    /**
     * Heals the combatant by the specified amount.
     * Health should not exceed the max cap (e.g., 100).
     *
     * @param amount Amount of HP to restore
     */
    void heal(int amount);

    /**
     * Checks whether the combatant is dead.
     * Used in battle resolution to determine the end of combat.
     *
     * @return true if health is 0 or less, false otherwise
     */
    boolean isDead();

    /**
     * Returns the base power (attack strength) of the combatant.
     * Used to calculate damage dealt to enemies.
     *
     * @return Integer power value (e.g., 5–15)
     */
    int getPower();

    /**
     * Determines if the combatant successfully dodges an incoming attack.
     * The result may be probabilistic or based on agility stats.
     *
     * @return true if the attack is evaded, false if hit
     */
    Boolean tryEvade();
}
