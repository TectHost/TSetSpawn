package managers;

import org.bukkit.configuration.file.FileConfiguration;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CooldownManager {
    private final TSetSpawn plugin;
    private final ConfigFile configFile;
    private final Map<Integer, Double> cooldowns = new HashMap<>();

    public CooldownManager(TSetSpawn plugin) {
        this.plugin = plugin;
        this.configFile = new ConfigFile("cooldowns.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadCooldowns();
    }

    private void loadCooldowns() {
        FileConfiguration config = configFile.getConfig();
        clear();

        if (config.contains("cooldowns")) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("cooldowns")).getKeys(false)) {
                try {
                    int id = Integer.parseInt(key);
                    double seconds = config.getDouble("cooldowns." + key);
                    cooldowns.put(id, seconds);
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("[CooldownManager] Invalid spawn ID in cooldowns.yml: \"" + key + "\" is not a number.");
                }
            }
        }
    }

    public double getCooldownFor(int spawnId) {
        return cooldowns.getOrDefault(spawnId, 0.0);
    }

    public void reloadCooldowns() {
        configFile.reloadConfig();
        loadCooldowns();
    }

    public void clear() {
        cooldowns.clear();
    }
}
