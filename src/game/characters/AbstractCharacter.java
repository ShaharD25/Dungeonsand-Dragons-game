//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.characters;
import game.combat.Combatant;
import game.core.GameEntity;

import game.map.Position;
import java.util.Random;

/**
 * Abstract base class for all characters (players and enemies) in the game.
 * Handles common properties like health, power, position, visibility, and damage logic.
 */
public abstract class AbstractCharacter implements Combatant, GameEntity {
    private Position position;
    private int health ;
    private int power;
    private final double evasionChance = 0.25;
    private boolean visible = false;

    public AbstractCharacter(Position position) {
        this.position = position;
        this.health = 100;
        this.power = (int)(Math.random() * 11) + 4; //
}

    // Getters and setters for position
    public Position getPosition() {
        return position;
    }

//    public void setPosition(Position position) {
//        this.position = position;
//    }

    public boolean setPosition(Position position) {
        if (position != null) {
            this.position = position;
            return true;
        }
        return false;
    }


    /**
     * Handles receiving damage from a source, considering evasion or accuracy.
     *
     * @param amount  The damage amount
     * @param source  The attacker
     */
    @Override
    public void receiveDamage(int amount, Combatant source){
        if(source instanceof Archer)
        {
            if(Math.random() > evasionChance * (1 - ((Archer)source).getAccuracy()))
            {
                this.health -= amount;
                if (health < 0)
                    health = 0;
            }
        }
        else if (!tryEvade()) {
                this.health -= amount;
                if (health < 0)
                    health = 0;
        }
    }

    /**
     * Returns whether the character is dead (health <= 0).
     */
    @Override
    public boolean isDead(){
        return health <= 0;
    }

    /**
     * Heals the character by the given amount (up to max 100).
     */
    @Override
    public void heal(int amount)
    {
        this.health += amount;
        if(health > 100){
            health = 100;
        }
    }

    // Power getter and setter
    public int getPower(){return power;}
    public void setPower(int power){ this.power = Math.max(1, power);}

    // Health getter and setter
    public int getHealth(){return health;}
    public void setHealth(int health){this.health = Math.max(0, Math.min(health, 100));}

    /**
     * Determines if the character dodges an attack (25% base chance).
     *
     * @return true if evaded, false otherwise
     */
    public Boolean tryEvade(){return Math.random() <= evasionChance;}

    /**
     * Updates the character's visibility state.
     *
     * @param visible true if visible on map
     */
    @Override
    public void setVisible(boolean visible) {this.visible = visible;}

    /**
     * Returns a simple string showing the character's current status.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " HP: " + health + " Power: " + power;
    }

    /**
     * Indicates whether this entity is currently visible to the player.
     * Used in map rendering and fog-of-war logic.
     *
     * @return true if the entity is visible, false otherwise.
     */
    public boolean isVisible() {return visible;}
}
