package managers;

import org.bukkit.configuration.file.FileConfiguration;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ActionBarManager {
    private final TSetSpawn plugin;
    private final ConfigFile configFile;
    private final Map<Integer, String> actionbar;

    public ActionBarManager(TSetSpawn plugin) {
        this.plugin = plugin;
        this.configFile = new ConfigFile("actionbar.yml", "modules", plugin);
        this.configFile.registerConfig();
        this.actionbar = new HashMap<>();
        loadTitles();
    }

    public void loadTitles() {
        FileConfiguration config = configFile.getConfig();
        clearActionBar();

        if (config.contains("actionbar")) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("actionbar")).getKeys(false)) {
                try {
                    int id = Integer.parseInt(key);
                    String message = config.getString("actionbar." + id);
                    actionbar.put(id, message != null ? message : "");
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Error loading spawn message with ID: " + key);
                }
            }
        }
    }

    public String getSpawnActionBar(int spawnId) {
        return actionbar.getOrDefault(spawnId, "");
    }

    public void reloadActionBar() {
        configFile.reloadConfig();
        clearActionBar();
        loadTitles();
    }

    public void clearActionBar() {
        actionbar.clear();
    }
}
