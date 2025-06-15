package game.decorators;

import game.characters.Enemy;
import game.map.Position;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Abstract decorator for enemies.
 * Wraps an existing Enemy and allows adding new behavior.
 */
public abstract class EnemyDecorator extends Enemy {

    protected final Enemy wrapped;

    public EnemyDecorator(Enemy wrapped) {

        super(null, null, null, 0, new Position(0, 0));
        this.wrapped = wrapped;
    }

    @Override
    public void takeAction() {
        wrapped.takeAction();
    }

    @Override
    public Position getPosition() {
        return wrapped.getPosition();
    }

    @Override
    public boolean setPosition(Position pos) {
        return wrapped.setPosition(pos);
    }

    @Override
    public boolean isDead() {
        return wrapped.isDead();
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
    public int getLoot() {
        return wrapped.getLoot();
    }

    @Override
    public boolean setLoot(int loot) {
        return wrapped.setLoot(loot);
    }

    @Override
    public void defeat() {
        wrapped.defeat();
    }

    @Override
    public void run() {
        wrapped.run();
    }

    @Override
    public void fightPlayer(game.characters.PlayerCharacter player) {
        wrapped.fightPlayer(player);
    }

    @Override
    public void moveToPlayer(game.characters.PlayerCharacter player) {
        wrapped.moveToPlayer(player);
    }

    @Override
    public void stopEnemy() {
        wrapped.stopEnemy();
    }

    @Override
    public void onPlayerMoved(Position pos) {
        wrapped.onPlayerMoved(pos);
    }

    @Override
    public String getOriginalName() {
        return wrapped.getOriginalName();
    }

}

