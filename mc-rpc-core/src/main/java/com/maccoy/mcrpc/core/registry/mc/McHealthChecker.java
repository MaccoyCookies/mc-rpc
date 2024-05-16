package com.maccoy.mcrpc.core.registry.mc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Maccoy
 * @date 2024/5/3 21:28
 * Description
 */
@Slf4j
public class McHealthChecker {

    ScheduledExecutorService consumerExecutor;

    ScheduledExecutorService providerExecutor;
    public void start() {
        consumerExecutor = Executors.newScheduledThreadPool(1);
        providerExecutor = Executors.newScheduledThreadPool(1);

    }

    public void stop() {
        gracefulShutdown(providerExecutor);
        gracefulShutdown(consumerExecutor);
    }

    public void providerCheck(Callback callback) {
        providerExecutor.scheduleWithFixedDelay(() -> {
            try {
                callback.call();
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public void consumerCheck(Callback callback) {
        consumerExecutor.scheduleWithFixedDelay(() -> {
            try {
                callback.call();
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
        }, 1, 5, TimeUnit.SECONDS);
    }

    private void gracefulShutdown(ScheduledExecutorService executorService) {
        executorService.shutdown();
        try {
            executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
            if (!executorService.isTerminated()) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            // ignore
        }
    }

    public interface Callback {
        void call() throws Exception;
    }
}
