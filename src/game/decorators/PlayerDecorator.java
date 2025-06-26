package game.decorators;

import game.characters.PlayerCharacter;
import game.combat.Combatant;
import game.gui.GameObserver;
import game.items.GameItem;
import game.map.Position;

/**
 * Base class for all player decorators.
 * Wraps a PlayerCharacter and delegates behavior.
 */
public abstract class PlayerDecorator extends PlayerCharacter {
    protected PlayerCharacter wrapped;

    public PlayerDecorator(PlayerCharacter player) {
        super(player.getName());
        setPosition(player.getPosition());
        this.wrapped = player;
    }

    public void setWrapped(PlayerCharacter wrapped) {
        this.wrapped = wrapped;
    }
    
    public PlayerCharacter getWrapped()
    {
    		return wrapped;
    }
    
    @Override
    public Position getPosition()
    {
    		if(wrapped == null)
    			return super.getPosition();
    		return wrapped.getPosition();
    }
    
    @Override
    public boolean setPosition(Position position)
    {
		if(wrapped == null)
			return super.setPosition(position);
    		return wrapped.setPosition(position);
    }
    
    @Override
    public void receiveDamage(int amount, Combatant source)
    {
    		if(wrapped == null)
    		{
    			super.receiveDamage(amount, source);
    			return;
    		}		
    		wrapped.receiveDamage(amount, source);
    }
    
    @Override
    public boolean isDead()
    {
    		if(wrapped == null)
			return super.isDead();
    		return wrapped.isDead();
    }
    
    @Override
    public void heal(int amount)
    {
		if(wrapped == null)
		{
			super.heal(amount);
			return;
		}
			 
    		wrapped.heal(amount);
    }
    
    
    @Override
    public Boolean tryEvade()
    {
    		if(wrapped == null)
			return super.tryEvade();
    		return wrapped.tryEvade();
    }
    
    @Override
    public void setVisible(boolean visible)
    {
    		if(wrapped == null)
    		{
    			super.setVisible(visible);
    			return;
    		}
			 
    		wrapped.setVisible(visible);
    }
    
    @Override
    public String toString()
    {
    		if(wrapped == null)
			return super.toString();
    		return wrapped.toString();
    }
    
    @Override
    public boolean isVisible()
    {
    		if(wrapped == null)
			return super.isVisible();
    		return wrapped.isVisible();
    }
     
    
    @Override
	public String getClassSimpleName()
	{
    		if(wrapped == null)
			return super.getClassSimpleName();
		return wrapped.getClassSimpleName();
	}
    
    @Override
    public int getHealth() {
    		if(wrapped == null)
			return super.getHealth();
        return wrapped.getHealth();
    }

    @Override
    public void setHealth(int health) {
    		if(wrapped == null)
    		{
    			super.setHealth(health);
    			return;
    		}
			
        wrapped.setHealth(health);
    }

    @Override
    public int getPower() {
    		if(wrapped == null)
			return super.getPower();
        return wrapped.getPower();
    }

    @Override
    public void setPower(int power) {
    		if(wrapped == null) {
    			super.setPower(power);
    			return;
    		}
			
        wrapped.setPower(power);
    }

    @Override
    public boolean moveToPosition(Position newPos)
    {
    		super.setPosition(newPos);
    		if(wrapped == null)
			return super.moveToPosition(newPos);
	    	return wrapped.moveToPosition(newPos);
    }
    
    @Override
    public void handleInteractions(Position pos)
    {
		if(wrapped == null) {
			super.handleInteractions(pos);
			return;
		}
			
    		wrapped.handleInteractions(pos);
    }
    
    @Override
    public String getName()
    {
		if(wrapped == null)
			return super.getName();
    		return wrapped.getName();
    }
    
    @Override
    public boolean addToInventory(GameItem item)
    {
		if(wrapped == null)
			return super.addToInventory(item);
    		return wrapped.addToInventory(item);
    }
    
    @Override
    public boolean usePotion()
    {
		if(wrapped == null)
			return super.usePotion();
    		return wrapped.usePotion();
    }
    
    @Override
    public boolean usePowerPotion()
    {
		if(wrapped == null)
			return super.usePowerPotion();
    		return wrapped.usePowerPotion();
    }
    
    @Override
    public boolean updateTreasurePoint(int amount)
    {
		if(wrapped == null)
			return super.updateTreasurePoint(amount);
    		return wrapped.updateTreasurePoint(amount);
    }
    
    @Override
    public int getTreasurePoints()
    {
		if(wrapped == null)
			return super.getTreasurePoints();
    		return wrapped.getTreasurePoints();
    }
    
    @Override
    public void addObserver(GameObserver observer)
    {
    		wrapped.addObserver(observer);
    }
    
    @Override
    public void removeObserver(GameObserver observer)
    {
    		wrapped.removeObserver(observer);
    }
    
    @Override
    public void notifyObservers()
    {
    		wrapped.notifyObservers();
    }
    
    @Override
    public void clearObservers()
    {
		wrapped.clearObservers();  	
    }
    
    @Override
    public int getPowerPotionCount()
    {
		if(wrapped == null)
			return super.getPowerPotionCount();
    		return wrapped.getPowerPotionCount();
    }
    
    @Override
    public int getLifePotionCount()
    {
		if(wrapped == null)
			return super.getLifePotionCount();
    		return wrapped.getLifePotionCount();
    }
    
    @Override
    public String getImagePath()
    {
		if(wrapped == null)
			return super.getImagePath();
    		return wrapped.getImagePath();
    }
    
    
    
    
}
