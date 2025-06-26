package game.decorators;

import game.audio.SoundPlayer;
import game.characters.PlayerCharacter;
import game.combat.Combatant;
import game.combat.CombatSystem;
import game.gui.PopupPanel;
import game.logging.LogManager;


/**
 * Decorator that provides a temporary shield to absorb one attack per combat round,
 * and reduces all incoming damage by 5%.
 */
public class ShieldedPlayerDecorator extends PlayerDecorator {

    private boolean shieldAvailable = true; // True if first attack is blocked

    public ShieldedPlayerDecorator(PlayerCharacter player) {
        super(player);
    }

    // Return whether the shield is still active
    public boolean isShieldActive() {
        return shieldAvailable;
    }

    // Mark the shield as used for this combat round
    public void consumeShield() {
        this.shieldAvailable = false;
    }

    // Optional override to show icon or symbol (no need to change if not needed)
    @Override
    public String getDisplaySymbol() {
        return wrapped.getDisplaySymbol() + "נ�›¡";
    }

    @Override
    public int getHealth() {
        return wrapped.getHealth();
    }

    @Override
    public void setHealth(int health) {
        // Reduce incoming damage by 5%
        int currentHealth = getHealth();
        int damage = currentHealth - health;

        if (damage > 0) {
            int reducedDamage = (int)(damage * 0.95); // apply 5% reduction
            int newHealth = currentHealth - reducedDamage;
            wrapped.setHealth(newHealth);
        } else {
            // Healing or no damage
            wrapped.setHealth(health);
        }
    }


    public PlayerCharacter getWrapped() {
        return wrapped;
    }


    @Override
    public void receiveDamage(int amount, Combatant source)
    {
        if (isShieldActive()) {
            consumeShield(); 
            PopupPanel.showPopup("Shield Blocked!", "Your shield blocked the incoming attack!");
            LogManager.log("Shield blocked damage!");
            SoundPlayer.playSound("shield.wav");
        } else {
            super.receiveDamage(amount,source);
            //defender.setHealth(defender.getHealth() - damage);
        }
    }


    @Override
    public String getImagePath() {
        return wrapped.getImagePath();
    }

}