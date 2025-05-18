//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.combat;
import game.audio.SoundPlayer;
import game.map.Position;
import game.characters.*;
import game.gui.PopupPanel;

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

        do {
            boolean isCrit = Math.random() < 0.2;
            if (isCrit) {
                PopupPanel.showPopup("Critical Hit!", "You landed a CRITICAL HIT!");
                SoundPlayer.playSound("critical-hit.wav");
            }

            if (attacker instanceof MagicAttacker magic) {
                if (isCrit && attacker instanceof PlayerCharacter pc) {
                    defender.setHealth(defender.getHealth() - pc.getPower() * 2);
                } else {
                    magic.castSpell(defender);
                    SoundPlayer.playSound("magic-spell.wav");
                }

            } else if (attacker instanceof PhysicalAttacker physical) {
                if (isCrit && attacker instanceof PlayerCharacter pc) {
                    defender.setHealth(defender.getHealth() - pc.getPower() * 2);
                    SoundPlayer.playSound("critical-hit.wav");//
                } else {
                    physical.attack(defender); //
                }

            } else {
                System.out.println("Unknown attacker");
            }

            if (defender.isDead()) {
                if (defender instanceof Enemy enemy) {
                    enemy.defeat();
                    SoundPlayer.playSound("kill.wav");
                    break;
                }
            }

            Combatant temp = defender;
            defender = attacker;
            attacker = temp;
        } while (!defender.isDead());



//        do{
//            // Perform the actual attack: either cast a spell or do a physical attack
//            if (attacker instanceof MagicAttacker magic) {
//                magic.castSpell(defender);
//            } else if (attacker instanceof PhysicalAttacker physical) {
//                physical.attack(defender);
//            }
//            else {
//                System.out.println("Unknown attacker");
//            }
//            // After attack: check if the defender died
//            if (defender.isDead())
//            {
//                if (defender instanceof Enemy enemy) // If the defender is an enemy, drop loot
//                {
//                    enemy.defeat();// Drop treasure on the map
//                    break;
//                }
//            }
//            Combatant temp = defender;
//            defender = attacker;
//            attacker = temp;
//        }while(!defender.isDead());
    }
}
