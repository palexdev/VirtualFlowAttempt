package io.github.palexdev.VirtualFlowAttempt.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.*;

public class LogSystem {
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2,
            4,
            10,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(),
            runnable -> {
                Thread thread = Executors.defaultThreadFactory().newThread(runnable);
                thread.setName("VFAttempt" + " - Executor");
                thread.setDaemon(true);
                return thread;
            }
    );

    static {
        executor.allowCoreThreadTimeOut(true);
    }

    public static void debug(String fileName, List<String> logs) {
        debug(fileName, logs, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static void debug(String fileName, List<String> logs, StandardOpenOption mode) {
        Callable<Void> debugTask = () -> {
            Path debug = Paths.get("").toAbsolutePath().resolve("logs/" + fileName);

            StringBuilder sb = new StringBuilder();
            logs.forEach(sb::append);

            try {
                if (!Files.exists(debug)) {
                    Files.createDirectories(debug.getParent());
                    Files.createFile(debug);
                }
                Files.writeString(debug, sb, mode);
            } catch (IOException ignored) {
            }

            return null;
        };
        executor.submit(debugTask);
    }

    public static void debug(String fileName, String s, StandardOpenOption mode) {
        Callable<Void> debugTask = () -> {
            Path debug = Paths.get("").toAbsolutePath().resolve("logs/" + fileName);

            try {
                if (!Files.exists(debug)) {
                    Files.createDirectories(debug.getParent());
                    Files.createFile(debug);
                }
                Files.writeString(debug, s, mode);
            } catch (IOException ignored) {
            }

            return null;
        };
        executor.submit(debugTask);
    }
}
