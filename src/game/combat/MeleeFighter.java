package game.combat;
import game.map.Position;
/**
 * Interface for characters who can perform melee (close-range) attacks.
 * Implemented by entities like Warrior, Orc, Goblin, etc.
 */
public interface MeleeFighter {

    /**
     * Performs a close-range attack on the target.
     * This is the core melee attack behavior.
     *
     * @param target The target combatant to attack
     */
    void fightClose(Combatant target);

    /**
     * Checks whether the target is within melee attack range.
     * Typically range is exactly 1 (adjacent tiles).
     *
     * @param self   The attacker's current position
     * @param target The target's current position
     * @return true if within range, false otherwise
     */
    boolean isInMeleeRange(Position self, Position target);
}
