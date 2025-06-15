// GameCaretaker.java
package game.memento;

import java.util.Deque;
import java.util.LinkedList;

/**
 * GameCaretaker manages a history of saved game states using the Memento design pattern.
 * It supports saving multiple states and restoring the most recent one.
 */
public class GameCaretaker {

    private final Deque<GameMemento> history = new LinkedList<>();

    /**
     * Saves a new snapshot of the game state.
     *
     * @param memento the game state to save
     */
    public void save(GameMemento memento) {
        history.push(memento);
    }

    /**
     * Restores the most recently saved game state.
     *
     * @return the last saved GameMemento, or null if history is empty
     */
    public GameMemento restore() {
        if (!history.isEmpty()) {
            return history.pop();
        }
        return null;
    }

    /**
     * Checks if there is at least one saved state.
     *
     * @return true if history is not empty
     */
    public boolean hasSavedState() {
        return !history.isEmpty();
    }

    /**
     * Clears all saved states.
     */
    public void clear() {
        history.clear();
    }
}
