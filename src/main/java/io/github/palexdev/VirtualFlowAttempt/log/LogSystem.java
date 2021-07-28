package io.github.palexdev.VirtualFlowAttempt.log;

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
        Callable<Void> debugTask = () -> {
            Path debug = Paths.get("").toAbsolutePath().resolve("logs/" + fileName);

            StringBuilder sb = new StringBuilder();
            logs.forEach(sb::append);

            try {
                if (!Files.exists(debug)) {
                    Files.createDirectories(debug.getParent());
                    Files.createFile(debug);
                }
                Files.writeString(debug, sb, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException ignored) {
            }

            return null;
        };
        executor.submit(debugTask);
    }
}
