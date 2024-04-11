package com.unirest.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("unused")
public class AsyncUtils {
    private static final ExecutorService executors = Executors.newFixedThreadPool(4);

    public static void async(Runnable runnable) {
        executors.execute(runnable);
    }

    public static void waitAsync(int millis, Runnable runnable) {
        async(() -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ignored) {
            } finally {
                runnable.run();
            }
        });
    }

    public static void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }

}
