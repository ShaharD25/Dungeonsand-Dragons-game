package game.decorators;

import game.characters.PlayerCharacter;
import game.combat.MeleeFighter;
import game.combat.PhysicalAttacker;
import game.combat.Combatant;
import game.map.Position;

/**
 * Decorator that boosts the player's attack power by a fixed amount.
 */
public class BoostedAttackDecorator extends PlayerDecorator implements PhysicalAttacker, MeleeFighter {

    private final int bonusDamage;

    public BoostedAttackDecorator(PlayerCharacter player, int bonusDamage) {
        super(player);
        this.bonusDamage = bonusDamage;
    }

	public int getPowerBoost() {
		return bonusDamage;
	}
    
    // Return boosted power
    @Override
    public int getPower() {
        return wrapped.getPower() + bonusDamage;
    }

    // Physical attack logic
    @Override
    public boolean attack(Combatant target) {
        if (wrapped instanceof PhysicalAttacker fighter) {
            return fighter.attack(target);
        }
        return false;
    }

    // Melee range check
    @Override
    public boolean isInMeleeRange(Position self, Position target) {
        if (wrapped instanceof MeleeFighter fighter) {
            return fighter.isInMeleeRange(self, target);
        }
        return false;
    }

    // Melee fight method
    @Override
    public void fightClose(Combatant target) {
        if (wrapped instanceof MeleeFighter fighter) {
            fighter.fightClose(target);
        } else {
            // Default fallback
            attack(target);
        }
    }

    // Critical hit check
    @Override
    public boolean isCriticalHit() {
        if (wrapped instanceof PhysicalAttacker attacker) {
            return attacker.isCriticalHit();
        }
        return false;
    }

    // Keep original symbol
    @Override
    public String getDisplaySymbol() {
        return wrapped.getDisplaySymbol();
    }

    // Expose inner player
    public PlayerCharacter getWrapped() {
        return wrapped;
    }

    @Override
    public String getImagePath() {
        return wrapped.getImagePath();
    }





}
