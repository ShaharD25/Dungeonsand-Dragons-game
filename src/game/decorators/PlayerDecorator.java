package game.decorators;

import game.characters.PlayerCharacter;

/**
 * Base class for all player decorators.
 * Wraps a PlayerCharacter and delegates behavior.
 */
public abstract class PlayerDecorator extends PlayerCharacter {
    protected PlayerCharacter wrapped;

    public PlayerDecorator(PlayerCharacter player) {
        super(player.getName());
        this.wrapped = player;
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
    public void setPower(int power) {
        wrapped.setPower(power);
    }

    // Add more delegation methods as needed...
}
