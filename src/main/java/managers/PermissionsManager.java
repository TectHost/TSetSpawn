package managers;

import org.bukkit.configuration.file.FileConfiguration;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PermissionsManager {
    private final TSetSpawn plugin;
    private final ConfigFile configFile;
    private final Map<Integer, String> permissions;

    public PermissionsManager(TSetSpawn plugin) {
        this.plugin = plugin;
        this.configFile = new ConfigFile("permissions.yml", "modules", plugin);
        this.configFile.registerConfig();
        this.permissions = new HashMap<>();
        loadTitles();
    }

    public void loadTitles() {
        FileConfiguration config = configFile.getConfig();
        clearPermissions();

        if (config.contains("permissions")) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("permissions")).getKeys(false)) {
                try {
                    int id = Integer.parseInt(key);
                    String message = config.getString("permissions." + id);
                    permissions.put(id, message != null ? message : "");
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Error loading spawn message with ID: " + key);
                }
            }
        }
    }

    public String getSpawnPermission(int spawnId) {
        return permissions.getOrDefault(spawnId, "");
    }

    public void reloadPermissions() {
        configFile.reloadConfig();
        clearPermissions();
        loadTitles();
    }

    public void clearPermissions() {
        permissions.clear();
    }
}
