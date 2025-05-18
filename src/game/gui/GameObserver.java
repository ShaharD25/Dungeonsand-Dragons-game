package game.gui;

import game.map.Position;

/**
 * Observer interface for updating the GUI when the game state changes.
 */
public interface GameObserver {
    void onPlayerMoved(Position pos);
}
