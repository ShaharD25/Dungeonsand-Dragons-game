package game.engine;

import game.characters.Enemy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EnemyPool {
    private static EnemyPool INSTANCE;                  // Singleton
    private final ExecutorService exec;

    private EnemyPool(int rows, int cols) {
        int poolSize = Math.max(1,Math.min(10, (int) (rows * cols * 0.03)));  // 3 %, 1-10
        this.exec = Executors.newFixedThreadPool(poolSize);
    }

    public static synchronized EnemyPool init(int rows, int cols) {
        if (INSTANCE == null) INSTANCE = new EnemyPool(rows, cols);
        return INSTANCE;
    }
    public static EnemyPool instance() { return INSTANCE; }

    /* schedule first tick of a freshly created enemy */
    public void scheduleEnemy(Enemy e) {
        if (!exec.isShutdown())
            exec.submit(e);
    }

    /* graceful shutdown from GameEngine.shutdown() */
    public void shutdown() {
        if (exec != null)
            exec.shutdownNow();
    }
}
