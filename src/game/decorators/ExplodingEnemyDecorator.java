package game.decorators;

import game.characters.Enemy;
import game.characters.PlayerCharacter;
import game.engine.GameWorld;
import game.map.Position;

import java.util.List;

public class ExplodingEnemyDecorator extends EnemyDecorator {

    public ExplodingEnemyDecorator(Enemy wrapped) {
        super(wrapped);
    }

    @Override
    public void defeat() {
        // Deal damage to nearby players before defeat
        Position pos = getPosition();
        List<PlayerCharacter> players = GameWorld.getInstance().getPlayers();

        for (PlayerCharacter player : players) {
            if (player.getPosition().distanceTo(pos) <= 1) {
                int damage = 10; // 20% of 50 max health
                player.setHealth(player.getHealth() - damage);
                System.out.println("[ExplodingEnemyDecorator] Explosion damaged nearby player for " + damage + " HP.");
            }
        }

        // Call original defeat behavior
        wrapped.defeat();
    }

    @Override
    public String getDisplaySymbol() {
        return wrapped.getDisplaySymbol();
    }
}
