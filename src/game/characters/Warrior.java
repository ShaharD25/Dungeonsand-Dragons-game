package game.characters;
import game.combat.Combatant;
import game.combat.MeleeFighter;
import game.combat.PhysicalAttacker;
import game.core.Inventory;
import game.map.Position;
import java.util.Random;

/**
 * Represents a Warrior player character.
 * Warriors are melee physical attackers with defensive capabilities and critical hit chance.
 */
public class Warrior extends PlayerCharacter implements PhysicalAttacker, MeleeFighter {
    private int defence;
    /**
     * Constructs a Warrior with a randomized defence value (0–120).
     *
     * @param name The name of the Warrior
     */
    public  Warrior(String name) {
        super(name);
        this.defence = new Random().nextInt(121);
    }

    /**
     * Receives damage, reduced by the warrior's defence value.
     * Includes evasion check before applying damage.
     */
    @Override
    public void receiveDamage(int amount, Combatant source){
        double damage = source.getPower() * (1 - Math.min(0.6,defence/200.0));
        if (!tryEvade()) {
            int currentHealth = getHealth();
            currentHealth -= damage;
            if (currentHealth < 0)
                currentHealth = 0;
            setHealth(currentHealth);
        }
    }

    /**
     * Performs a physical attack on a target, using calculated damage.
     */
    @Override
    public void attack(Combatant target) {
        int damage = calculateDamage();
        target.receiveDamage(damage, this);
    }

    /**
     * Returns true if the attack is a critical hit (10% chance).
     */
    @Override
    public boolean isCriticalHit(){
        return Math.random() <= 0.1;
    }

    /**
     * Performs a melee (close-range) attack.
     */
    @Override
    public void fightClose(Combatant target){attack(target);}

    /**
     * Checks if the target is within melee range (distance ≤ 1).
     */
    @Override
    public boolean isInMeleeRange(Position self, Position target) {
        return self.distanceTo(target) <= 1;
    }

    /**
     * Returns the warrior's defence value.
     */
    public int getDefence() {
        return defence;
    }

    /**
     * Sets the warrior's defence, clamped between 0 and 120.
     */
    public void setDefence(int defence) {
        if (defence >= 0 && defence <= 120)
            this.defence = defence;
    }

    /**
     * Returns the symbol used to represent the Warrior on the map.
     */
    @Override
    public String getDisplaySymbol() {
        return "W";
    }

    /**
     * Whether the Warrior is visible on the map.
     * Default is false – should be updated externally via visibility logic.
     */
    @Override
    public boolean isVisible() {
        return false;
    }

    /**
     * Calculates attack damage.
     * If critical hit, returns double power.
     */
    public int calculateDamage() {
        if (isCriticalHit()) {
            return getPower() * 2;
        } else {
            return getPower();
        }
    }
}
