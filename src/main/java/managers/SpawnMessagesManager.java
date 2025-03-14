package managers;

import org.bukkit.configuration.file.FileConfiguration;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SpawnMessagesManager {
    private final TSetSpawn plugin;
    private final ConfigFile configFile;
    private final Map<Integer, String> spawnMessages;

    public SpawnMessagesManager(TSetSpawn plugin) {
        this.plugin = plugin;
        this.configFile = new ConfigFile("messages.yml", "modules", plugin);
        this.configFile.registerConfig();
        this.spawnMessages = new HashMap<>();
        loadMessages();
    }

    public void loadMessages() {
        FileConfiguration config = configFile.getConfig();
        spawnMessages.clear();

        if (config.contains("messages")) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("messages")).getKeys(false)) {
                try {
                    int id = Integer.parseInt(key);
                    String message = config.getString("messages." + id);
                    spawnMessages.put(id, message != null ? message : "");
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Error loading spawn message with ID: " + key);
                }
            }
        }
    }

    public String getSpawnMessage(int spawnId) {
        return spawnMessages.getOrDefault(spawnId, "");
    }

    public void reloadMessages() {
        configFile.reloadConfig();
        loadMessages();
    }
}
