package managers;

import org.bukkit.configuration.file.FileConfiguration;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

public class ConfigManager {
    private final ConfigFile configFile;

    private String langFile;

    public ConfigManager(TSetSpawn plugin) {
        this.configFile = new ConfigFile("config.yml", null, plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = configFile.getConfig();

        langFile = config.getString("general.lang-file");
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public String getLangFile() { return langFile; }
}
