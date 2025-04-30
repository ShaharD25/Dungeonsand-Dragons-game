//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.characters;
import game.combat.Combatant;
import game.combat.MeleeFighter;
import game.combat.PhysicalAttacker;
import game.map.Position;

import java.util.Random;
/**
 * Represents a Goblin enemy character.
 * Goblins are agile melee physical attackers with a chance to dodge attacks.
 */
public class Goblin extends Enemy implements MeleeFighter, PhysicalAttacker {
    private int agility;

    /**
     * Constructs a Goblin with given loot and position.
     * Agility is randomized between 0 and 80.
     *
     * @param loot     The value of treasure dropped on defeat
     * @param position The goblin's initial location on the board
     */
    public Goblin(int loot, Position position) {
        super(loot, position);
        this.agility = new Random().nextInt(81);
    }

    /**
     * Performs a close-range melee attack using this goblin's logic.
     */
    @Override
    public void fightClose(Combatant target){attack(target);}

    /**
     * Calculates whether the goblin evades an incoming attack.
     * Max evade chance is capped at 80%.
     */
    @Override
    public Boolean tryEvade(){
        return Math.random() <= Math.min(0.8,this.agility/100.0);
    }

    /**
     * Checks if the target is within melee attack range (distance = 1).
     */
    @Override
    public boolean isInMeleeRange(Position self, Position target) {
        return self.distanceTo(target) == 1;
    }

    /**
     * Performs a physical attack by dealing calculated damage to the target.
     */
    @Override
    public void attack(Combatant target){
        int damage = calculateDamage();
        target.receiveDamage(damage,this);
    }

    /**
     * Determines if the current attack is a critical hit (10% chance).
     */
    @Override
    public boolean isCriticalHit(){return Math.random() < 0.1;}

    /**
     * Returns the goblin's agility value.
     */
    public int getAgility() {
        return agility;
    }

    /**
     * Sets the goblin's agility, capped between 0–80.
     */
    public boolean setAgility(int agility) {
        if (agility >= 0 && agility <= 80) {
            this.agility = agility;
            return true;
        }
        return false;
    }

    /**
     * Returns the character used to represent the goblin on the map.
     */
    @Override
    public String getDisplaySymbol() {
        return "G";
    }

    /**
     * Determines if the goblin is visible on the map.
     * (Default is false — can be updated by visibility logic.)
     */
    @Override
    public boolean isVisible() {
        return false;
    }

    /**
     * Calculates the total damage dealt by this combatant.
     * If the attack is a critical hit (based on probability), the damage is doubled.
     *
     * @return The final damage amount to be applied to the target
     */
    public int calculateDamage() {
        if (isCriticalHit()) {
            return getPower() * 2;
        } else {
            return getPower();
        }
    }
}
