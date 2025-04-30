//Artiom Bondar:332692730
//Shahar Dahan: 207336355
package game.characters;
import game.items.Treasure;
import game.map.Position;
import game.engine.GameWorld;
import java.util.Random;

/**
 * Abstract class representing an enemy character on the map.
 * Enemies have loot value and drop a treasure upon defeat.
 */
public abstract class Enemy extends AbstractCharacter{
    private int loot;
    /**
     * Constructs an enemy with given loot and a fixed position.
     * Health is randomly initialized between 0–50.
     *
     * @param loot     The value of the treasure the enemy will drop
     * @param position The position of the enemy on the map
     */
    public Enemy(int loot, Position position){
        super(position);
        setHealth(new Random().nextInt(51));
        this.loot = loot;
    }

    /**
     * Triggers upon the enemy's defeat.
     * A Treasure is created and added to the map at the enemy's position.
     */
    public void defeat()
    {
        Position p = this.getPosition();
        Treasure t = new Treasure(p,true,loot);
        GameWorld.getInstance().getMap().addEntity(p,t);
    }

    /**
     * Returns the amount of treasure (loot points) this enemy is holding.
     *
     * @return The loot value of the enemy.
     */
    public int getLoot() {
        return loot;
    }

    /**
     * Sets the loot value of the enemy.
     * Only accepts non-negative values (0 or more).
     *
     * @param loot The new loot value to assign.
     */
    public boolean setLoot(int loot) {
        if (loot >= 0) {
            this.loot = loot;
            return true;
        }
        return false;
    }

}
