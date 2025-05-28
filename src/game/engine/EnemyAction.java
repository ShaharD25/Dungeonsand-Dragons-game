package game.engine;

import game.characters.Enemy;
import game.characters.PlayerCharacter;

public class EnemyAction {
    private final Enemy enemy;
    private final PlayerCharacter player;
    private final boolean isFight;

    public EnemyAction(Enemy enemy, PlayerCharacter player, boolean isFight) {
        this.enemy = enemy;
        this.player = player;
        this.isFight = isFight;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public PlayerCharacter getPlayer() {
        return player;
    }

    public boolean isFight() {
        return isFight;
    }
}
