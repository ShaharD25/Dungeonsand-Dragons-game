//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.combat;
import game.map.Position;
import game.characters.*;
import game.characters.PlayerCharacter;

/**
 * Utility class for resolving combat between two combatants.
 * Supports physical and magical combat, with melee and ranged checks,
 * evasion, and elemental comparison logic.
 */
public class CombatSystem {
    /**
     * Resolves a full combat encounter between two combatants:
     * - Checks melee/ranged distance
     * - Handles evasion
     * - Applies damage (magic or physical)
     * - Checks for death and triggers defeat if applicable
     *
     * @param attacker The entity initiating the attack
     * @param defender The entity being attacked
     */
    public static void resolveCombat(Combatant attacker, Combatant defender) {
      Position attackerPos = attacker.getPosition();
      Position defenderPos = defender.getPosition();

        // Melee attack range validation
        if (attacker instanceof MeleeFighter melee){
          if (!melee.isInMeleeRange(attackerPos,defenderPos)){
          System.out.println("You are not in melee range");
          return;
          }
      }

        // Ranged attack range validation
      if (attacker instanceof RangedFighter ranged){
          if (!ranged.isInRange(attackerPos,defenderPos)){
              System.out.println("You are not in range range");
              return;
          }
      }

        // Check if defender evades the attack
      if (defender.tryEvade())
      {
          System.out.println("You are evadable");
          return;
      }

        // If both attacker and defender use magic, compare their elements
        if(attacker instanceof MagicAttacker magicAttacker &&
      defender instanceof MagicAttacker magicDefender){
          if (magicAttacker.isElementStrongerThan(magicDefender)){
              System.out.println("You are stronger than magic attacker");
          }
          else if(!magicAttacker.getElement().equals(magicDefender.getElement()) &&
            !magicAttacker.getElement().isStrongerThan(magicDefender.getElement())){
              System.out.println("You are not stronger than magic attacker");
          }
      }
        // Perform the actual attack: either cast a spell or do a physical attack
        if (attacker instanceof MagicAttacker magic) {
            magic.castSpell(defender);
        } else if (attacker instanceof PhysicalAttacker physical) {
            physical.attack(defender);
        }
        else {
            System.out.println("Unknown attacker");
        }
        // After attack: check if the defender died
        if (defender.isDead())
        {
            System.out.println(defender.getDisplaySymbol() + "You are dead");
            if (defender instanceof Enemy enemy) // If the defender is an enemy, drop loot
            {
                enemy.defeat();// Drop treasure on the map
            }
        }


    }

}
