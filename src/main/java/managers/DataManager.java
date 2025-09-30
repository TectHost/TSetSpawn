package managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

import java.util.Objects;
import java.util.UUID;

public class DataManager {
    private final ConfigFile configFile;
    private FileConfiguration config;

    public DataManager(TSetSpawn plugin) {
        this.configFile = new ConfigFile("players.yml", null, plugin);
        loadConfig();
    }

    private void loadConfig() {
        config = configFile.getConfig();
    }

    public boolean hasJoinedBefore(@NotNull UUID uuid) {
        return config.getBoolean("players." + uuid, false);
    }

    public void setJoined(@NotNull UUID uuid) {
        config.set("players." + uuid, true);
        configFile.saveConfig();
    }

    public int getRegisteredPlayersCount() {
        if (!config.contains("players")) {
            return 1;
        }
        return Objects.requireNonNull(config.getConfigurationSection("players")).getKeys(false).size() + 1;
    }
}
