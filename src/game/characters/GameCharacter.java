package game.characters;
import game.map.Position;

public interface GameCharacter {
    int getHealth();
    void setHealth(int health);
    int getPower();
    void setPower(int power);
    boolean moveToPosition(Position position);
    void handleInteractions(Position pos);
    String getDisplaySymbol();
}
