//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.combat;

import game.audio.SoundPlayer;
import game.logging.LogManager;
import game.map.Position;
import game.characters.*;
import game.decorators.EnemyDecorator;
import game.decorators.PlayerDecorator;
import game.decorators.ShieldedPlayerDecorator;
import game.gui.PopupPanel;

/**
 * Utility class for resolving combat between two combatants.
 */
public class CombatSystem {

    public static void resolveCombat(Combatant attacker, Combatant defender, boolean isHero) {
        Position attackerPos = attacker.getPosition();
        Position defenderPos = defender.getPosition();

        Combatant OriginalAttacker = attacker, OriginalDefender = defender;
        while(attacker instanceof PlayerDecorator decAtt)
        	{
        		attacker = decAtt.getWrapped();
        	}
        while(attacker instanceof EnemyDecorator decAtt)
        	{
        		attacker = decAtt.getWrapped();
        	}
        	
        while(defender instanceof PlayerDecorator decDef)
        	{
        		defender = decDef.getWrapped();
        	} 	
        while(defender instanceof EnemyDecorator decDef)
        	{
        		defender = decDef.getWrapped();
        	}
        	
        	
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
        		
            int damage = OriginalDefender.getHealth();
            boolean isCrit = false;
            Combatant InnerOriginalAttacker = OriginalAttacker;
            if (attacker instanceof PhysicalAttacker) {       		             
                	while(InnerOriginalAttacker instanceof PlayerDecorator decAtt)
                	{
                		if(!(InnerOriginalAttacker instanceof PhysicalAttacker))
                			InnerOriginalAttacker = decAtt.getWrapped();
                		else break;
                	}
                	while(InnerOriginalAttacker instanceof EnemyDecorator decAtt)
                	{
                		if(!(InnerOriginalAttacker instanceof PhysicalAttacker))
                			InnerOriginalAttacker = decAtt.getWrapped();
                		else break;
                	}
                	
	            	 if(attacker instanceof Dragon dragon && dragon.isInRange(attackerPos,defenderPos) && !dragon.isInMeleeRange(attackerPos, defenderPos))
	             {
	                	//dragon.castSpell(OriginalDefender);
	                	
             		if(InnerOriginalAttacker instanceof MagicAttacker)
             			((MagicAttacker)InnerOriginalAttacker).castSpell(OriginalDefender);
             		else
             			dragon.castSpell(OriginalDefender);
	             }
	            	 else              	
	            		 isCrit = ((PhysicalAttacker)InnerOriginalAttacker).attack(OriginalDefender);

                    
            }
            else
            {
                if(attacker instanceof Mage mage && mage.isInRange(attackerPos,defenderPos))
                {
	                	while(InnerOriginalAttacker instanceof PlayerDecorator decAtt)
	                	{
	                		if(!(InnerOriginalAttacker instanceof MagicAttacker))
	                			InnerOriginalAttacker = decAtt.getWrapped();
	                		else break;
	                	}
	                	while(InnerOriginalAttacker instanceof EnemyDecorator decAtt)
	                	{
	                		if(!(InnerOriginalAttacker instanceof MagicAttacker))
	                			InnerOriginalAttacker = decAtt.getWrapped();
	                		else break;
	                	}
                		((MagicAttacker)InnerOriginalAttacker).castSpell(OriginalDefender);
                }
            }

            if (isCrit) {
                String msg = isHero ? "You landed a CRITICAL HIT!" : "Enemy landed a CRITICAL HIT!";
                PopupPanel.showPopup("Critical Hit!", msg);
                LogManager.log(msg);
                SoundPlayer.playSound("critical-hit.wav");
            }

            damage = damage - OriginalDefender.getHealth();
            String who = isHero ? "You" : "Enemy";
            PopupPanel.showPopup("Damage Dealt", who + " dealt " + damage + " damage");


            if (OriginalDefender.isDead()) {
                if (OriginalDefender instanceof Enemy enemy) {
                    enemy.defeat();
                    SoundPlayer.playSound("kill.wav");
                }
                break;
            }
            
            if(isHero)
            {
            		inRange = false;
                if (defender instanceof RangedFighter ranged) {
                    if (!ranged.isInRange(attackerPos, defender.getPosition())) {
                        return;
                    }
                    inRange = true;
                }

                if (!inRange) {
                    if (defender instanceof MeleeFighter melee) {
                        if (!melee.isInMeleeRange(attackerPos, defender.getPosition())) {
                            return;
                        }
                    }
                }
            }
            

            Combatant temp = defender;
            defender = attacker;
            attacker = temp;
            
            temp = OriginalDefender;
            OriginalDefender = OriginalAttacker;
            OriginalAttacker = temp;
            
            isHero = !isHero;
            attackerPos = attacker.getPosition();
            defenderPos = defender.getPosition();

        } while (!defender.isDead());
    }
}
