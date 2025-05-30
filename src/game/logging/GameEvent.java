package game.logging;
import java.time.LocalDateTime;
/**
 * GameEvent represents a single log entry in the game.
 * It stores a textual description and a timestamp indicating when the event occurred.
 */
public class GameEvent {
    private final String description;
    private final LocalDateTime timestamp;

    /**
     * Constructs a new GameEvent with the given description.
     * Automatically assigns the current date and time as the timestamp.
     *
     * @param description A string describing the event
     */
    public GameEvent(String description) {
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }
    /**
     * Returns the description of the event.
     *
     * @return A string describing the event
     */
    public String getDescription() {return description;}

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    /**
     * Returns a formatted string representation of the event,
     * including the timestamp and description.
     *
     * @return A string in the format: [timestamp] description
     */
    @Override
    public String toString() {
        return "[" + timestamp + "] " + description;
    }

}
