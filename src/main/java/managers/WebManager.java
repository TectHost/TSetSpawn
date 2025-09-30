package managers;

import org.bukkit.configuration.file.FileConfiguration;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

public class WebManager {

    private final ConfigFile configFile;

    private int port;

    public WebManager(TSetSpawn plugin) {
        this.configFile = new ConfigFile("web.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfiguration();
    }

    public void loadConfiguration() {
        FileConfiguration config = configFile.getConfig();

        port = config.getInt("general.port", 8080);
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfiguration();
    }

    public int getPort() {
        return port;
    }
}
