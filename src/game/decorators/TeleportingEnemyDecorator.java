package game.decorators;

import game.characters.Enemy;
import game.characters.PlayerCharacter;
import game.engine.GameWorld;
import game.map.GameMap;
import game.map.Position;

import java.util.Random;

/**
 * Enemy decorator that teleports the enemy to a random position on the map
 * if its health drops below 30% after being attacked.
 */
public class TeleportingEnemyDecorator extends EnemyDecorator {

    private boolean hasTeleported = false;

    public TeleportingEnemyDecorator(Enemy enemy) {
        super(enemy);
    }

    @Override
    public void fightPlayer(PlayerCharacter player) {
        wrapped.fightPlayer(player);

        if (!hasTeleported && wrapped.getHealth() < 15) { // 30% of 50 = 15
            teleportRandomly();
            hasTeleported = true;
        }
    }

    private void teleportRandomly() {
        GameMap map = GameWorld.getInstance().getMap();
        Random rand = new Random();

        for (int attempts = 0; attempts < 50; attempts++) {
            int row = rand.nextInt(map.getMapSize());
            int col = rand.nextInt(map.getMapSize());
            Position newPos = new Position(row, col);

            if (map.isValidPosition(newPos) && !map.isWall(newPos) && map.getEntitiesAt(newPos).isEmpty()) {
                Position current = wrapped.getPosition();
                map.moveEntity(GameWorld.getInstance(), wrapped, current, newPos);
                wrapped.setPosition(newPos);
                System.out.println("[TeleportingEnemyDecorator] Enemy teleported to: " + newPos);
                return;
            }
        }

        System.err.println("[TeleportingEnemyDecorator] Teleport failed: No empty position found.");
    }

    @Override
    public String getDisplaySymbol() {
        return wrapped.getDisplaySymbol();
    }
}
