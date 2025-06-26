package game.builder;

import game.characters.Enemy;
import game.map.Position;

import java.lang.reflect.Constructor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static game.engine.GameWorld.EXEC;
import static game.engine.GameWorld.BOARD_LOCK;
import static game.engine.GameWorld.gameRunning;

public class EnemyBuilder {
    private int health = 30; // Default health
    private int power = 10;  // Default power
    private int loot = 5;    // Default loot value
    private Position position = new Position(0, 0); // Default starting position
    private Class<? extends Enemy> type; // Enemy type to instantiate

    public EnemyBuilder setType(Class<? extends Enemy> type) {
        this.type = type;
        return this;
    }

    public EnemyBuilder setPower(int power) {
        this.power = power;
        return this;
    }

    public EnemyBuilder setLoot(int loot) {
        this.loot = loot;
        return this;
    }

    public EnemyBuilder setPosition(Position position) {
        this.position = position;
        return this;
    }

    public EnemyBuilder setHealth(int health) {
        this.health = health;
        return this;
    }

    public EnemyBuilder setRandom() {
        this.health = ThreadLocalRandom.current().nextInt(1, 51);
        this.power = ThreadLocalRandom.current().nextInt(4, 15);
        this.loot = ThreadLocalRandom.current().nextInt(10, 51);
        return this;
    }

    public Enemy build() {
        try {
            Constructor<? extends Enemy> constructor =
                    type.getConstructor(ScheduledExecutorService.class, AtomicBoolean.class, ReentrantLock.class, int.class, Position.class);
            Enemy enemy = constructor.newInstance(EXEC, gameRunning, BOARD_LOCK, loot, position);


            enemy.setHealth(health);
            enemy.setPower(power);
            
            return enemy;
        } catch (Exception e) {
            throw new RuntimeException("Failed to build enemy: " + type.getSimpleName(), e);
        }
    }
}
