package managers;

import org.bukkit.configuration.file.FileConfiguration;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

public class JoinManager {
    private final ConfigFile configFile;

    private boolean spawnOnJoinEnabled;
    private int spawnOnJoinId;
    private boolean firstJoinSpawnEnabled;
    private int firstJoinSpawnId;

    public JoinManager(TSetSpawn plugin) {
        this.configFile = new ConfigFile("join.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = configFile.getConfig();

        spawnOnJoinEnabled = config.getBoolean("join.spawn-on-join");
        spawnOnJoinId = config.getInt("join.spawn-on-join-id");
        firstJoinSpawnEnabled = config.getBoolean("join.first-join-spawn");
        firstJoinSpawnId = config.getInt("join.first-join-spawn-id");
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public boolean isSpawnOnJoinEnabled() {
        return spawnOnJoinEnabled;
    }
    public int getSpawnOnJoinId() {
        return spawnOnJoinId;
    }
    public boolean isFirstJoinSpawnEnabled() {
        return firstJoinSpawnEnabled;
    }
    public int getFirstJoinSpawnId() {
        return firstJoinSpawnId;
    }
}
