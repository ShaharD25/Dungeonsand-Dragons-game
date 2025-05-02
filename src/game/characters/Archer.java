//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.characters;
import game.combat.Combatant;
import game.combat.PhysicalAttacker;
import game.combat.RangedFighter;
import game.core.Inventory;
import game.map.Position;
/**
 * Represents a player-controlled Archer character.
 * Archer performs ranged physical attacks with a chance to hit based on accuracy.
 */
public class Archer extends PlayerCharacter implements PhysicalAttacker, RangedFighter {
    private double accuracy;

    /**
     * Constructs an Archer with a name and randomized accuracy (0.0 to 0.8).
     *
     * @param name The name of the player character
     */
    public Archer(String name) {
        super(name);
        this.accuracy = Math.random() * 0.8;
    }

    // --- Getters and Setters ---
    /**
     * Gets the Archer's accuracy.
     */
    public double getAccuracy() {return accuracy;}
    public void setAccuracy(double accuracy) {this.accuracy = accuracy;}


    // --- Combat Logic ---
    /**
     * Calculates the attack damage.
     * Doubles the power if it's a critical hit.
     *
     * @return The total calculated damage
     */
    public int calculateDamage() {
        if (isCriticalHit()) {
            return getPower() * 2;
        } else {
            return getPower();
        }
    }


    /**
     * Performs a physical attack on the target by applying calculated damage.
     *
     * @param target The combatant being attacked
     */
    public void attack(Combatant target){
        int damage = calculateDamage();
        target.receiveDamage(damage,this);
    }

    /**
     * Determines whether the current attack is a critical hit (10% chance).
     */
    public boolean isCriticalHit(){return Math.random() < 0.1;}

    // --- RangedFighter Implementation ---
    /**
     * Performs a ranged attack (delegates to attack).
     */
    public void fightRanged(Combatant target){attack(target);}


    /**
     * Returns the maximum range for the Archer's attack.
     */
    public int getRange(){return 2;}


    /**
     * Checks if the target is within the Archer's range (Manhattan distance ≤ 2).
     *
     * @param self   The Archer's position
     * @param target The target's position
     * @return true if within range
     */
    public boolean isInRange(Position self, Position target){return self.distanceTo(target) <=2;}


    /**
     * Returns the symbol used to represent the Archer on the map.
     *
     * @return "A" for Archer
     */
    @Override
    public String getDisplaySymbol() {
        return "A";
    }


}
