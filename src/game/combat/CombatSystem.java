//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.combat;
import game.audio.SoundPlayer;
import game.logging.LogManager;
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
    public static void resolveCombat(Combatant attacker, Combatant defender, boolean isHero) {
        Position attackerPos = attacker.getPosition();
        Position defenderPos = defender.getPosition();

        boolean inRange = false;
        // Ranged attack range validation
        if (attacker instanceof RangedFighter ranged) {
            if (!ranged.isInRange(attackerPos, defenderPos)) {
                if (isHero)
                    System.out.println("You are not in range range");
                return;
            }
            inRange = true;
        }

        if(!inRange)
        {
            // Melee attack range validation
            if (attacker instanceof MeleeFighter melee) {
                if (!melee.isInMeleeRange(attackerPos, defenderPos)) {
                    if (isHero)
                        System.out.println("You are not in melee range");
                    return;
                }
            }
        }



        do {
            boolean isCrit = Math.random() < 0.2;
            if (isCrit) {
                if (isHero)
                {
                    PopupPanel.showPopup("Critical Hit!", "You landed a CRITICAL HIT!");
                    LogManager.log("Critical Hit! You landed a CRITICAL HIT!");
                }

                else {
                    PopupPanel.showPopup("Critical Hit!", "Enemy landed a CRITICAL HIT!");
                    LogManager.log("Critical Hit! Enemy landed a CRITICAL HIT!");
                }
                SoundPlayer.playSound("critical-hit.wav");
            }

            if(attacker instanceof Dragon dragon) {
                if(!dragon.isInRange(attackerPos, defenderPos))
                {
                    if (isCrit) {
                        defender.setHealth(defender.getHealth() - attacker.getPower() * 2);
                        SoundPlayer.playSound("critical-hit.wav");
                    } else {
                        dragon.fightClose(defender); //
                        SoundPlayer.playSound("critical-hit.wav"); //CHANGE to normal hit sound
                    }
                }
                else {
                    if (isCrit) {
                        defender.setHealth(defender.getHealth() - attacker.getPower() * 2);
                    } else {
                        dragon.fightRanged(defender);
                        SoundPlayer.playSound("magic-spell.wav");
                    }
                }
            }

            else {
                if (attacker instanceof MagicAttacker magic) {
                    if (isCrit) {
                        defender.setHealth(defender.getHealth() - attacker.getPower() * 2);
                    } else {
                        magic.castSpell(defender);
                        SoundPlayer.playSound("magic-spell.wav");
                    }

                } else if (attacker instanceof PhysicalAttacker physical) {
                    if (isCrit) {
                        defender.setHealth(defender.getHealth() - attacker.getPower() * 2);
                        SoundPlayer.playSound("critical-hit.wav");//
                    } else {
                        physical.attack(defender); //
                        SoundPlayer.playSound("critical-hit.wav"); //CHANGE to normal hit sound
                    }

                } else {
                    System.out.println("Unknown attacker");
                }

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
            isHero = !isHero;
        } while (!defender.isDead());

    }

}
