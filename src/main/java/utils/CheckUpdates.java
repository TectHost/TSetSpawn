package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class CheckUpdates {

    private static final String RESOURCE_URL = "https://api.spigotmc.org/legacy/update.php?resource=111179";

    public static void check(Logger logger, String currentVersion, Consumer<String> versionCallback) {
        new Thread(() -> {
            try {
                URL url = new URL(RESOURCE_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int status = connection.getResponseCode();
                if (status != 200) {
                    logger.warning("Failed to check for updates. HTTP response code: " + status);
                    return;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String latestVersion = reader.readLine();
                reader.close();

                if (latestVersion == null || latestVersion.isEmpty()) {
                    logger.warning("Spigot returned an empty version.");
                    return;
                }

                if (latestVersion.equalsIgnoreCase(currentVersion)) {
                    logger.info("You are using the latest version (" + currentVersion + ").");
                } else {
                    logger.warning("A new version is available: " + latestVersion + " (You have " + currentVersion + ")");
                }

                versionCallback.accept(latestVersion);

            } catch (Exception e) {
                logger.warning("Failed to check version: " + e.getMessage());
            }
        }).start();
    }
}
