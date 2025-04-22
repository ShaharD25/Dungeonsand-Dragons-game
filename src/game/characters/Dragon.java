package game.characters;

import game.Main;
import game.combat.*;
import game.map.Position;


/**
 * Represents a Dragon enemy that can attack with both physical and magical abilities.
 * Implements Melee, Ranged, Physical and Magical combat behaviors.
 */
public class Dragon extends Enemy implements MagicAttacker, PhysicalAttacker, MeleeFighter, RangedFighter {
    private MagicElement element;
    /**
     * Constructs a Dragon with a given loot value and position.
     * Randomly assigns a magic element using the main game utility.
     *
     * @param loot     Treasure value dropped upon defeat
     * @param position The dragon's starting location
     */
    public Dragon(int loot, Position position){
        super(loot, position);
        this.element = Main.getRandomElement();
    }


    /**
     * Calculates and applies magic damage to a target.
     * Damage is increased or decreased based on elemental strengths.
     *
     * @param target The combatant being attacked
     */
    @Override
    public void calculateMagicDamage(Combatant target) {
        double base = getPower() * 1.5;
       if (target instanceof MagicAttacker) {
            MagicAttacker other = (MagicAttacker) target;
            if (isElementStrongerThan(other)) {
                base *= 1.2;
            } else if (!this.element.equals(other.getElement()) && !this.element.isStrongerThan(other.getElement())) {
                base *= 0.8;
            }
        }
        target.receiveDamage((int) base, this);
    }

    /**
     * Performs a magic attack using the dragon's current element.
     */
    @Override
    public void castSpell(Combatant target){calculateMagicDamage(target);}

    /**
     * Returns the dragon's elemental type (FIRE, ICE, etc.).
     */
    @Override
    public MagicElement getElement(){return element;}

    /**
     * Compares this dragon's element with another to determine elemental advantage.
     */
    @Override
    public boolean isElementStrongerThan(MagicAttacker other){ return this.element.isStrongerThan(other.getElement());}

    /**
     * Not used directly in this implementation (replaced by melee or ranged logic).
     */
    public void attack(Combatant target){}

    /**
     * Determines if the current physical attack is a critical hit (10% chance).
     */
    @Override
    public boolean isCriticalHit(){return Math.random() < 0.1;}


    /**
     * Returns the dragon's elemental type (FIRE, ICE, etc.).
     */
    public int calculateDamage() {
        if (isCriticalHit()) {
            return getPower() * 2;
        } else {
            return getPower();
        }
    }

    /**
     * Performs a melee attack (close range) by applying physical damage.
     */
    @Override
    public void fightClose(Combatant target){
        int damage = calculateDamage();
        target.receiveDamage(damage, this);
    }

    /**
     * Checks if the target is within melee range (distance == 1).
     */
    @Override
    public boolean isInMeleeRange(Position self, Position target){return self.distanceTo(target) == 1;}

    /**
     * Performs a ranged magic attack.
     */
    @Override
    public void fightRanged(Combatant target){castSpell(target);}

    /**
     * Returns the attack range for ranged magic.
     */
    @Override
    public int getRange(){return 2;}

    /**
     * Checks if the target is within ranged distance (≤ range).
     */
    @Override
    public boolean isInRange(Position self, Position target){return self.distanceTo(target) <= getRange();}


    /**
     * Symbol to represent the dragon on the game map.
     */
    @Override
    public String getDisplaySymbol() {
        return "D";
    }


    /**
     * Whether this dragon is currently visible to the player.
     * By default, false (updated by visibility logic in game).
     */
    @Override
    public boolean isVisible() {
        return false;
    }
}
