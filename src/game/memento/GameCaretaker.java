package game.memento;
import javax.swing.text.Caret;
import java.util.Deque;
import java.util.LinkedList;
public class GameCaretaker {
    private final Deque<GameMemento> history = new LinkedList<>();
    public void save(GameMemento memento) {
        history.push(memento);
    }

    public GameMemento restore() {
        if (!history.isEmpty()) {
            return history.pop();
        }
        return null;
    }

    public boolean hasSavedState(){return !history.isEmpty();}
}
