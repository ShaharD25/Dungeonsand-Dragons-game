package game.decorators;

import game.audio.SoundPlayer;
import game.characters.Enemy;
import game.characters.PlayerCharacter;
import game.combat.Combatant;
import game.engine.GameWorld;
import game.gui.PopupPanel;
import game.logging.LogManager;
import game.map.GameMap;
import game.map.Position;

import java.util.Random;

/**
 * Enemy decorator that teleports the enemy to a random position on the map
 * if its health drops below 30% after being attacked.
 */
public class TeleportingEnemyDecorator extends EnemyDecorator {

    private boolean hasTeleported = false;
    private int startingHealth;
    
    public TeleportingEnemyDecorator(Enemy enemy) {
        super(enemy);
        startingHealth = wrapped.getHealth();
    }
    
    public boolean hasTeleported() {
        return hasTeleported;
    }

    public void setHasTeleported(boolean teleported) {
        this.hasTeleported = teleported;
    }
    
    @Override
    public void receiveDamage(int amount, Combatant source)
    {
    		wrapped.receiveDamage(amount,source);
        if (!hasTeleported && wrapped.getHealth() > 0 && wrapped.getHealth() < startingHealth*0.3) { 
            teleportRandomly();
            hasTeleported = true;
            wrapped.active.set(false);
        }
    }

//    @Override
//    public void fightPlayer(PlayerCharacter player) {
//        wrapped.fightPlayer(player);
//    }

    private void teleportRandomly() {
    		GameWorld world = GameWorld.getInstance();
    		Position newPos = world.getFreeRandomPosition();
    		synchronized (world.getMap()) {
                world.getMap().moveEntity(world, wrapped, wrapped.getPosition(), newPos);
            }
    		System.out.println("[TeleportingEnemyDecorator] Enemy teleported to: " + newPos);
    }

    @Override
    public String getDisplaySymbol() {
        return wrapped.getDisplaySymbol();
    }
}
