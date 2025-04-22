package game.items;
import game.map.Position;
/**
 * Represents a wall on the game board.
 * Walls block movement and cannot be interacted with.
 */
public class Wall extends GameItem{
    /**
     * Constructs a wall at the specified position.
     *
     * @param position The location of the wall on the board
     */
    public Wall(Position position) {
        super(position,true,"Wall",true, "W");
    }

    @Override
    public boolean isVisible() {return false;}
}
