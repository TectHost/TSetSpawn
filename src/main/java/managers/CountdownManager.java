package managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

import java.util.*;

public class CountdownManager {

    private final TSetSpawn plugin;
    private final ConfigFile configFile;
    private final Map<Integer, CountdownConfig> countdowns = new HashMap<>();

    public CountdownManager(TSetSpawn plugin) {
        this.plugin = plugin;
        this.configFile = new ConfigFile("countdowns.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadCountdowns();
    }

    private void loadCountdowns() {
        countdowns.clear();

        FileConfiguration config = configFile.getConfig();
        ConfigurationSection section = config.getConfigurationSection("countdowns");

        if (section == null) return;

        for (String key : section.getKeys(false)) {
            try {
                int spawnId = Integer.parseInt(key);
                ConfigurationSection spawnSection = section.getConfigurationSection(key);

                if (spawnSection == null) continue;

                double duration = spawnSection.getDouble("duration", 0.0);
                boolean cancelOnMove = spawnSection.getBoolean("cancel-on-move", false);

                String cancelSound = spawnSection.getString("cancel-sound", null);

                Map<Integer, String> messages = new HashMap<>();
                ConfigurationSection messagesSection = spawnSection.getConfigurationSection("messages");

                if (messagesSection != null) {
                    for (String secondKey : messagesSection.getKeys(false)) {
                        try {
                            int second = Integer.parseInt(secondKey);
                            String message = messagesSection.getString(secondKey, "");
                            messages.put(second, message);
                        } catch (NumberFormatException e) {
                            plugin.getLogger().warning("[CountdownManager] Invalid second in countdown message: " + secondKey);
                        }
                    }
                }

                countdowns.put(spawnId, new CountdownConfig(duration, cancelOnMove, cancelSound, messages));

            } catch (NumberFormatException e) {
                plugin.getLogger().warning("[CountdownManager] Invalid spawn ID in countdowns.yml: " + key);
            }
        }
    }

    public CountdownConfig getCountdown(int spawnId) {
        return countdowns.getOrDefault(spawnId, new CountdownConfig(0.0, false, null, Collections.emptyMap()));
    }

    public void reloadCountdowns() {
        configFile.reloadConfig();
        loadCountdowns();
    }

    public void clearCountdowns() {
        countdowns.clear();
    }

    public static class CountdownConfig {
        private final double duration;
        private final boolean cancelOnMove;
        private final String cancelSound;
        private final Map<Integer, String> messages;

        public CountdownConfig(double duration, boolean cancelOnMove, String cancelSound, Map<Integer, String> messages) {
            this.duration = duration;
            this.cancelOnMove = cancelOnMove;
            this.cancelSound = cancelSound;
            this.messages = messages;
        }

        public double getDuration() {
            return duration;
        }

        public boolean isCancelOnMove() {
            return cancelOnMove;
        }

        public String getCancelSound() {
            return cancelSound;
        }

        public Map<Integer, String> getMessages() {
            return messages;
        }

        public Optional<String> getMessageForSecond(int second) {
            return Optional.ofNullable(messages.get(second));
        }
    }
}
