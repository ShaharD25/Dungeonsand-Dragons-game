package game.logging;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.*;
public class LogManager {
    private static final BlockingQueue<GameEvent> eventQueue = new LinkedBlockingQueue<>();
    private static final ExecutorService loggerExecutor = Executors.newSingleThreadExecutor();
    private static final String LOG_FILE = "game_log.txt";

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


    public static void log(String message) {
        eventQueue.offer(new GameEvent(message));
    }

    public static void shutdown() {
        loggerExecutor.shutdownNow();
    }
}
