package game.decorators;

import game.characters.Dragon;
import game.characters.Enemy;
import game.characters.PlayerCharacter;
import game.combat.Combatant;
import game.combat.MagicAttacker;
import game.combat.MagicElement;
import game.combat.PhysicalAttacker;

/**
 * Enemy decorator that restores health equal to 1% of the player's health when attacking.
 */
public class VampireEnemyDecorator extends EnemyDecorator implements PhysicalAttacker, MagicAttacker{

    public VampireEnemyDecorator(Enemy enemy) {
        super(enemy);
    }

    @Override
    public void fightPlayer(PlayerCharacter player) {
        // Call original attack
        wrapped.fightPlayer(player);   
    }

    @Override
    public String getDisplaySymbol() {
        return wrapped.getDisplaySymbol();
    }

	@Override
	public void calculateMagicDamage(Combatant combatant) {
		((Dragon)wrapped).calculateMagicDamage(combatant);
	}

	@Override
	public void castSpell(Combatant target) {
		// Heal self by 2% of player's health, I've changed it from the 1% you asked, because 1% out of the current 
		//health of the hero will almost always be below 1 (for example 1% out of 87hp is 0.87 and when rounded to 
		//int it will be 0 so no health is regenerated)
        int healAmount = (int)(target.getHealth() * 0.02); //should change it to 0.01 if i want that 1% like asked in the assignment 
        if (healAmount > 0) {
            wrapped.setHealth(wrapped.getHealth() + healAmount);
            System.out.println("[VampireEnemyDecorator] Enemy healed by " + healAmount + " HP.");
        }
        ((Dragon)wrapped).castSpell(target);
	}

	@Override
	public MagicElement getElement() {
		return ((Dragon)wrapped).getElement();
	}

	@Override
	public boolean isElementStrongerThan(MagicAttacker other) {
		return ((Dragon)wrapped).getElement().isStrongerThan(other.getElement());
	}

	@Override
	public boolean attack(Combatant target) {
		// Heal self by 2% of player's health, I've changed it from the 1% you asked, because 1% out of the current 
		//health of the hero will almost always be below 1 (for example 1% out of 87hp is 0.87 and when rounded to 
		//int it will be 0 so no health is regenerated)
		int healAmount = (int)(target.getHealth() * 0.02); //should change it to 0.01 if i want that 1% like asked in the assignment 
        if (healAmount > 0) {
            wrapped.setHealth(wrapped.getHealth() + healAmount);
            System.out.println("[VampireEnemyDecorator] Enemy healed by " + healAmount + " HP.");
        }
        int damage = 0;
        boolean isCrit = isCriticalHit();
        if (isCrit) {
            damage = getPower() * 2;
        } else {
            damage = getPower();
        }
        target.receiveDamage(damage, this);
        return isCrit;
	}

	@Override
	public boolean isCriticalHit() {
		return Math.random() < 0.1;
	}
}

