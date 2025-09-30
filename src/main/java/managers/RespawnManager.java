package managers;

import org.bukkit.configuration.file.FileConfiguration;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

public class RespawnManager {
    private final ConfigFile configFile;

    private int id;

    public RespawnManager(TSetSpawn plugin) {
        this.configFile = new ConfigFile("respawn.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = configFile.getConfig();

        id = config.getInt("respawn.spawn-id");
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public int getId() {
        return id;
    }
}
