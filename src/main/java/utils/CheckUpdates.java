package utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.logging.Logger;

public final class CheckUpdates {

    private static final String DEFAULT_RESOURCE_URL = "https://api.spigotmc.org/legacy/update.php?resource=111179";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(5);

    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "TSetSpawn-CheckUpdates");
        t.setDaemon(true);
        return t;
    });

    private CheckUpdates() { /* util class */ }

    public static void check(Logger logger, String currentVersion, Consumer<String> versionCallback) {
        check(logger, DEFAULT_RESOURCE_URL, currentVersion, versionCallback, null, DEFAULT_TIMEOUT);
    }

    public static void check(Logger logger,
                             String resourceUrl,
                             String currentVersion,
                             Consumer<String> versionCallback,
                             Executor callbackExecutor,
                             Duration timeout) {
        EXECUTOR.submit(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(resourceUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout((int) timeout.toMillis());
                connection.setReadTimeout((int) timeout.toMillis());
                connection.setInstanceFollowRedirects(true);
                connection.setRequestProperty("User-Agent", "TSetSpawn-Updater");

                int status = connection.getResponseCode();
                if (status < 200 || status >= 300) {
                    logger.warning("Failed to check for updates. HTTP response code: " + status);
                    return;
                }

                try (InputStream in = connection.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {

                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append('\n');
                    }

                    String latest = sb.toString().trim();
                    if (latest.isEmpty()) {
                        logger.warning("Spigot returned an empty version.");
                        return;
                    }

                    if (latest.equalsIgnoreCase(currentVersion)) {
                        logger.info("You are using the latest version (" + currentVersion + ").");
                    } else {
                        logger.warning("A new version is available: " + latest + " (You have " + currentVersion + ")");
                    }

                    if (callbackExecutor != null) {
                        callbackExecutor.execute(() -> safeAccept(versionCallback, latest, logger));
                    } else {
                        safeAccept(versionCallback, latest, logger);
                    }
                }
            } catch (Exception e) {
                logger.warning("Failed to check version: " + e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private static void safeAccept(Consumer<String> consumer, String value, Logger logger) {
        try {
            consumer.accept(value);
        } catch (Throwable t) {
            logger.warning("Version callback threw an exception: " + t.getMessage());
            t.printStackTrace();
        }
    }

    public static void shutdown() {
        EXECUTOR.shutdownNow();
    }
}
