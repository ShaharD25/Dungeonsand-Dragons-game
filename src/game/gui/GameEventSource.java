package game.gui;

public interface GameEventSource {
    void addObserver(GameObserver go);
    void removeObserver(GameObserver go);
}
