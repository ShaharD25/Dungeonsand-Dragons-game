package game.items;
import game.characters.PlayerCharacter;
import game.items.GameItem;
import game.items.Interactable;
import game.map.Position;

import java.util.Random;
/**
 * Represents a treasure item placed on the map.
 * When interacted with, it adds points to the player's score.
 */
public class Treasure extends GameItem implements Interactable{
    private int value;
    private boolean collected;
    /**
     * Constructs a treasure with a name and point value.
     *
     * @param position The position on the board
     * @param value    The treasure point value
     */
    public Treasure(Position position, boolean visible,int value) {
        super(position, false, "Treasure", visible,"T");
        this.value = value;
        //this.value = new Random().nextInt(201)+100;
        this.collected = false;
    }

    /**
     * When the player interacts with this treasure, they gain points.
     * The treasure then disappears from the map.
     *
     * @param c The player collecting the treasure
     */
    @Override
    public void interact(PlayerCharacter c) {
        int randomNumber = new Random().nextInt(3);
        if(randomNumber == 0){
            c.addToInventory(new Potion(new Position(0,0)));
        }
        randomNumber = new Random().nextInt(2);
        if(randomNumber == 0){
            c.updateTreasurePoint(this.value);
        }
        randomNumber = new Random().nextInt(6);
        if(randomNumber == 0){
            c.addToInventory(new PowerPotion(new Position(0,0)));
        }
        this.collected = true;
    }

    @Override
    public boolean isVisible() {
        return false;
    }
}
