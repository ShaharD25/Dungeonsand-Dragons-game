//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.characters;
import game.combat.Combatant;
import game.combat.MagicAttacker;
import game.combat.MagicElement;
import game.combat.RangedFighter;
import game.core.Inventory;
import game.map.Position;

import java.util.Random;
/**
 * Represents a Mage character controlled by the player.
 * Mages perform ranged magical attacks using an elemental affinity (FIRE, ICE, etc.).
 */
public class Mage extends PlayerCharacter implements MagicAttacker, RangedFighter {
    private MagicElement element;
    /**
     * Constructs a Mage with a random magic element.
     *
     * @param name The name of the player-controlled Mage
     */
    public Mage(String name){
        super(name);
        int randomNumber = new Random().nextInt(4);
        switch (randomNumber) {
            case 0:
                this.element = MagicElement.FIRE;
                break;
            case 1:
                this.element = MagicElement.ICE;
                break;
            case 2:
                this.element = MagicElement.LIGHTNING;
                break;
            case 3:
                this.element = MagicElement.ACID;
                break;
        }
    }

    /**
     * Calculates the magical damage based on element and power.
     * Applies elemental strengths and weaknesses if the target is also a magic attacker.
     */
    @Override
    public void calculateMagicDamage(Combatant target) {
        double baseDamage = getPower() * 1.5;

        if (target instanceof MagicAttacker) {
            MagicElement targetElement = ((MagicAttacker) target).getElement();

            if (element.isStrongerThan(targetElement)) {
                baseDamage *= 1.2;
            } else if (targetElement.isStrongerThan(element)) {
                baseDamage *= 0.8;
            }
        }

        target.receiveDamage((int) baseDamage, this);
    }

    /**
     * Casts a spell on a target (delegates to calculateMagicDamage).
     */
    @Override
    public void castSpell(Combatant target){calculateMagicDamage(target);}

    /**
     * Returns the Mage's current magic element.
     */
    @Override
    public MagicElement getElement(){return element; }

    /**
     * Determines if this Mage's element is stronger than another's.
     */
    @Override
    public boolean isElementStrongerThan(MagicAttacker other){
        return this.element.isStrongerThan(other.getElement());
    }

    /**
     * Performs a ranged magic attack.
     */
    @Override
    public void fightRanged(Combatant target){castSpell(target);}

    /**
     * Returns the maximum range for magic attacks (2 tiles).
     */
    @Override
    public int getRange(){return 2;}

    /**
     * Checks if the target is within casting range.
     */
    @Override
    public boolean isInRange(Position self, Position target) {
        return self.distanceTo(target) <= 2;
    }

    /**
     * Returns the symbol used to represent the Mage on the map.
     */
    @Override
    public String getDisplaySymbol() {
        return "M";
    }
}
