package game.decorators;

import game.characters.PlayerCharacter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import game.engine.GameWorld;

/**
 * Decorator that provides passive health regeneration.
 * Heals the player by 2 HP every 10 seconds, up to a maximum of 100 HP.
 */
public class RegenerationDecorator extends PlayerDecorator {
    private Timer regenTimer;

    public RegenerationDecorator(PlayerCharacter player) {
        super(player);
        startRegeneration();
    }

    /**
     * Starts the regeneration timer that heals the player every 10 seconds.
     */
    private void startRegeneration() {
        regenTimer = new Timer(10000, new ActionListener() { // 10 seconds
            @Override
            public void actionPerformed(ActionEvent e) {
                int currentHp = wrapped.getHealth();
                int maxHp = 100;
                int regenAmount = 2;
                if (currentHp < maxHp) {
                    int newHp = Math.min(maxHp, currentHp + regenAmount);
                    wrapped.setHealth(newHp);
                    System.out.println("[Regeneration] HP regenerated to " + newHp);
                    GameWorld.getInstance().getGameFrame().getStatusPanel().updateStatus((PlayerCharacter)wrapped);
                }
            }
        });
        regenTimer.setRepeats(true);
        regenTimer.start();
    }

    @Override
    public int getHealth() {
        return wrapped.getHealth();
    }

    @Override
    public void setHealth(int health) {
        wrapped.setHealth(health);
    }

    @Override
    public int getPower() {
        return wrapped.getPower();
    }

    @Override
    public String getDisplaySymbol() {
        return wrapped.getDisplaySymbol();
    }

    @Override
    public String getImagePath() {
        return wrapped.getImagePath();
    }

    /**
     * Stops the regeneration effect.
     * Can be called manually if needed.
     */
    public void stopRegeneration() {
        if (regenTimer != null) {
            regenTimer.stop();
        }
    }

    public PlayerCharacter getWrapped() {
        return wrapped;
    }
}
