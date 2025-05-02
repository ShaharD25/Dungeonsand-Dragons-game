//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.characters;
import game.combat.Combatant;
import game.combat.MagicAttacker;
import game.combat.MeleeFighter;
import game.combat.PhysicalAttacker;
import game.map.Position;

import java.util.Random;
/**
 * Represents an Orc enemy character.
 * Orcs are melee physical attackers with resistance to magical damage.
 */
public class Orc extends Enemy implements MeleeFighter, PhysicalAttacker {
    private double resistance;

    /**
     * Constructs an Orc with randomized magic resistance (0.0 to 0.5).
     *
     * @param loot     The amount of treasure dropped on defeat
     * @param position Initial position of the Orc on the board
     */
    public Orc(int loot, Position position) {
        super(loot, position);
        this.resistance = Math.random() * 0.5;
    }

    /**
     * Performs a melee attack (close-range).
     */
    @Override
    public void fightClose(Combatant target){attack(target);}

    /**
     * Checks if the target is within melee range (distance == 1).
     */
    @Override
    public boolean isInMeleeRange(Position self, Position target){return self.distanceTo(target) == 1;}

    /**
     * Performs a physical attack, calculating damage (with critical chance).
     */
    @Override
    public void attack(Combatant target) {
        int damage = calculateDamage();
        target.receiveDamage(damage, this);
    }

    /**
     * Determines whether the attack is a critical hit (10% chance).
     */
    @Override
    public boolean isCriticalHit(){return Math.random() < 0.1;}

    /**
     * Returns the symbol that represents the Orc on the map.
     */
    @Override
    public String getDisplaySymbol() {
        return "O";
    }



    /**
     * Applies incoming damage, reducing it if the source is magical.
     * Magic attacks are reduced by a percentage defined by resistance.
     *
     * @param amount The incoming damage amount
     * @param source The attacker who dealt the damage
     */
    @Override
    public void receiveDamage(int amount, Combatant source) {
        int finalDamage = amount;

        if (source instanceof MagicAttacker) {
            finalDamage = (int)(amount * (1 - resistance));
        }
        super.receiveDamage(finalDamage, source);
    }


    /**
     * Calculates the damage to deal in a physical attack.
     * Doubles the damage if a critical hit occurs.
     */
    public int calculateDamage() {
        if (isCriticalHit()) {
            return getPower() * 2;
        } else {
            return getPower();
        }
    }
}
