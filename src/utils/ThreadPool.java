package utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    private static final ExecutorService SERVICE = Executors.newFixedThreadPool(4);

    private ThreadPool() {

    }

    public static void execute(Runnable action) {
        SERVICE.execute(action);
    }
}
