//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.combat;

import game.audio.SoundPlayer;
import game.logging.LogManager;
import game.map.Position;
import game.characters.*;
import game.decorators.ShieldedPlayerDecorator;
import game.gui.PopupPanel;

/**
 * Utility class for resolving combat between two combatants.
 */
public class CombatSystem {

    public static void resolveCombat(Combatant attacker, Combatant defender, boolean isHero) {
        Position attackerPos = attacker.getPosition();
        Position defenderPos = defender.getPosition();

        boolean inRange = false;

        if (attacker instanceof RangedFighter ranged) {
            if (!ranged.isInRange(attackerPos, defenderPos)) {
                if (isHero) System.out.println("You are not in range");
                return;
            }
            inRange = true;
        }

        if (!inRange) {
            if (attacker instanceof MeleeFighter melee) {
                if (!melee.isInMeleeRange(attackerPos, defenderPos)) {
                    if (isHero) System.out.println("You are not in melee range");
                    return;
                }
            }
        }

        do {
            boolean isCrit = Math.random() < 0.2;
            int power = attacker.getPower();
            int damage = isCrit ? power * 2 : power;

            if (isCrit) {
                String msg = isHero ? "You landed a CRITICAL HIT!" : "Enemy landed a CRITICAL HIT!";
                PopupPanel.showPopup("Critical Hit!", msg);
                LogManager.log(msg);
                SoundPlayer.playSound("critical-hit.wav");
            }

            // בדיקה אם המגן פעיל אצל המגן
            if (defender instanceof ShieldedPlayerDecorator shielded && shielded.isShieldActive()) {
                shielded.consumeShield(); // מוריד את המגן לסיבוב הבא
                PopupPanel.showPopup("Shield Blocked!", "Your shield blocked the incoming attack!");
                LogManager.log("Shield blocked damage!");
                SoundPlayer.playSound("shield.wav");
            } else {
                defender.setHealth(defender.getHealth() - damage);
            }

            // הצגת הנזק שנגרם
            String who = isHero ? "You" : "Enemy";
            PopupPanel.showPopup("Damage Dealt", who + " dealt " + damage + " damage. Power used: " + power);

            if (defender.isDead()) {
                if (defender instanceof Enemy enemy) {
                    enemy.defeat();
                    SoundPlayer.playSound("kill.wav");
                    break;
                }
            }

            // תחלופת תוקף ומגן
            Combatant temp = defender;
            defender = attacker;
            attacker = temp;
            isHero = !isHero;

        } while (!defender.isDead());
    }
}
