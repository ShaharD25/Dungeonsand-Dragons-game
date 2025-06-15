package game.decorators;

import game.characters.Enemy;
import game.characters.PlayerCharacter;

/**
 * Enemy decorator that restores health equal to 1% of the player's health when attacking.
 */
public class VampireEnemyDecorator extends EnemyDecorator {

    public VampireEnemyDecorator(Enemy enemy) {
        super(enemy);
    }

    @Override
    public void fightPlayer(PlayerCharacter player) {
        // Call original attack
        wrapped.fightPlayer(player);

        // Heal self by 1% of player's health
        int healAmount = (int)(player.getHealth() * 0.01);
        if (healAmount > 0) {
            wrapped.setHealth(wrapped.getHealth() + healAmount);
            System.out.println("[VampireEnemyDecorator] Enemy healed by " + healAmount + " HP.");
        }
    }

    @Override
    public String getDisplaySymbol() {
        return wrapped.getDisplaySymbol();
    }
}

