package game.logging;
import java.time.LocalDateTime;

public class GameEvent {
    private final String description;
    private final LocalDateTime timestamp;
    public GameEvent(String description) {
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public String getDescription() {return description;}

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + description;
    }

}
