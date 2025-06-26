package game.decorators;

import game.characters.Enemy;
import game.characters.PlayerCharacter;
import game.combat.RangedFighter;
import game.engine.GameWorld;
import game.map.GameMap;
import game.map.Position;


/**
 * Abstract decorator for enemies.
 * Wraps an existing Enemy and allows adding new behavior.
 */
public abstract class EnemyDecorator extends Enemy {

    protected final Enemy wrapped;

    public EnemyDecorator(Enemy wrapped) {
	    	super(wrapped.getExec(),        // אותו ScheduledExecutorService
	  	          wrapped.getRunning(),     // אותו דגל running
	  	          wrapped.getBoardLock(),   // אותו Lock
	  	          wrapped.getLoot(),   // שלל
	  	          wrapped.getPosition());
        setPosition(wrapped.getPosition());
        setHealth(wrapped.getHealth());
        this.wrapped = wrapped;
    }

	public Enemy getWrapped() {
		return wrapped;
	}
    
	@Override
	public void takeAction() {
	    // Instead of delegating to wrapped, do own logic
	    if (boardLock.tryLock()) {
	        try {
	            GameWorld world = GameWorld.getInstance();
	            PlayerCharacter player = world.getPlayers().get(0);

	            int fightRange = (this instanceof RangedFighter) ? 2 : 1;

	            if (GameMap.calcDistance(player.getPosition(), getPosition()) <= fightRange) {
	                fightPlayer(player);  // Now will call VampireEnemyDecorator.fightPlayer()
	            } else {
	                moveToPlayer(player);
	            }
	        } finally {
	            boardLock.unlock();
	        }
	    }
	}


    @Override
    public Position getPosition() {
	    	if(wrapped == null)
	    	{
	    		return super.getPosition();
	    	}
        return wrapped.getPosition();
    }

    @Override
    public boolean setPosition(Position pos) {
	    	if(wrapped == null)
	    	{    		
	    		return super.setPosition(pos);
	    	}
	    	super.setPosition(pos);
        return wrapped.setPosition(pos);
    }

    @Override
    public boolean isDead() {
	    	if(wrapped == null)
	    	{    		
	    		return super.isDead();
	    	}
        return wrapped.isDead();
    }

    @Override
    public int getHealth() {
	    	if(wrapped == null)
	    	{    		
	    		return super.getHealth();
	    	}
        return wrapped.getHealth();
    }

    @Override
    public void setHealth(int health) {
	    	if(wrapped == null)
	    	{    		
	    		super.setHealth(health);
	    		return ;
	    	}
        wrapped.setHealth(health);
        super.setHealth(health);
    }

    @Override
    public int getLoot() {
	    	if(wrapped == null)
	    	{    		
	    		return super.getLoot();
	    	}
        return wrapped.getLoot();
    }

    @Override
    public boolean setLoot(int loot) {
	    	if(wrapped == null)
	    	{    		
	    		return super.setLoot(loot);
	    	}
	    	super.setLoot(loot);
        return wrapped.setLoot(loot);
    }

    @Override
    public void defeat() {
	    	if(wrapped == null)
	    	{    		
	    		super.defeat();
	    		return ;
	    	}
        wrapped.defeat();
    }

    @Override
    public void run() {
	    	if(wrapped == null)
	    	{    		
	    		super.run();
	    		return ;
	    	}
        wrapped.run();
    }

//    @Override
//    public void fightPlayer(game.characters.PlayerCharacter player) {
//	    	if(wrapped == null)
//	    	{    		
//	    		super.fightPlayer(player);
//	    		return ;
//	    	}
//        wrapped.fightPlayer(player);
//    }

    @Override
    public void moveToPlayer(game.characters.PlayerCharacter player) {
	    	if(wrapped == null)
	    	{    		
	    		super.moveToPlayer(player);
	    		return ;
	    	}
        wrapped.moveToPlayer(player);
    }

    @Override
    public void stopEnemy() {
	    	if(wrapped == null)
	    	{    		
	    		super.stopEnemy();
	    		return ;
	    	}
        wrapped.stopEnemy();
    }

    @Override
    public void onPlayerMoved(Position pos) {
	    	if(wrapped == null)
	    	{    		
	    		super.onPlayerMoved(pos);
	    		return ;
	    	}
        wrapped.onPlayerMoved(pos);
    }

    @Override
    public String getOriginalName() {
	    	if(wrapped == null)
	    	{    		
	    		return super.getOriginalName();
	    	}
        return wrapped.getOriginalName();
    }

    @Override
    public void setInitialRandomHealth() 
    {
	    	if(wrapped == null)
	    	{    		
	    		super.setInitialRandomHealth();
	    		return ;
	    	}
    		wrapped.setInitialRandomHealth();
    }
   

}

