package managers;

import org.bukkit.configuration.file.FileConfiguration;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TitlesManager {
    private final TSetSpawn plugin;
    private final ConfigFile configFile;
    private final Map<Integer, String> titles;

    public TitlesManager(TSetSpawn plugin) {
        this.plugin = plugin;
        this.configFile = new ConfigFile("titles.yml", "modules", plugin);
        this.configFile.registerConfig();
        this.titles = new HashMap<>();
        loadTitles();
    }

    public void loadTitles() {
        FileConfiguration config = configFile.getConfig();
        clearTitles();

        if (config.contains("titles")) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("titles")).getKeys(false)) {
                try {
                    int id = Integer.parseInt(key);
                    String message = config.getString("titles." + id);
                    titles.put(id, message != null ? message : "");
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Error loading spawn title with ID: " + key);
                }
            }
        }
    }

    public String getSpawnTitle(int spawnId) {
        return titles.getOrDefault(spawnId, "");
    }

    public void reloadTitles() {
        configFile.reloadConfig();

        loadTitles();
    }

    public void clearTitles() {
        titles.clear();
    }
}
