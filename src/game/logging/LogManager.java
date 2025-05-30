package game.logging;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.*;
/**
 * LogManager handles asynchronous logging of game events.
 * It uses a single-threaded executor and a blocking queue to manage and write logs to a file.
 */
public class LogManager {
    // Blocking queue to hold log events waiting to be written to the log file

    private static final BlockingQueue<GameEvent> eventQueue = new LinkedBlockingQueue<>();
    private static final ExecutorService loggerExecutor = Executors.newSingleThreadExecutor();
    private static final String LOG_FILE = "game_log.txt";

    // Path to the log file
    static {
        loggerExecutor.submit(() -> {
            try (FileWriter writer = new FileWriter(LOG_FILE, false)) {
                while (true) {
                    GameEvent event = eventQueue.take(); // wait for event
                    writer.write(event.toString() + "\n");
                    writer.flush();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Adds a new log message to the event queue.
     * @param message The log message to record.
     */
    public static void log(String message) {
        eventQueue.offer(new GameEvent(message));
    }
    /**
     * Shuts down the logger thread immediately.
     * Called when the game exits to terminate the logging service.
     */
    public static void shutdown() {
        loggerExecutor.shutdownNow();
    }
}
