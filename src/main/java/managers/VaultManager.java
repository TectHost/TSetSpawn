package managers;

import org.bukkit.configuration.file.FileConfiguration;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VaultManager {
    private final TSetSpawn plugin;
    private final ConfigFile configFile;

    private final Map<Integer, Double> prices;

    public VaultManager(TSetSpawn plugin) {
        this.plugin = plugin;
        this.configFile = new ConfigFile("vault.yml", "hooks", plugin);
        this.configFile.registerConfig();
        this.prices = new HashMap<>();
        loadSpawns();
    }

    public void loadSpawns() {
        FileConfiguration config = configFile.getConfig();
        clearSpawns();

        if (config.contains("prices")) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("prices")).getKeys(false)) {
                try {
                    int id = Integer.parseInt(key);
                    double price = config.getDouble("prices." + id);
                    prices.put(id, price);
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Error loading spawn price with ID: " + key);
                }
            }
        }
    }

    public double getSpawnPrice(int spawnId) {
        return prices.getOrDefault(spawnId, 0.0);
    }

    public void reloadSpawns() {
        configFile.reloadConfig();
        loadSpawns();
    }

    public void clearSpawns() {
        prices.clear();
    }
}
