package managers;

import org.bukkit.configuration.file.FileConfiguration;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

public class SpawnListManager {

    private final ConfigFile configFile;
    private FileConfiguration config;

    private boolean guiEnabled;

    public SpawnListManager(TSetSpawn plugin) {
        this.configFile = new ConfigFile("list.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    private void loadConfig() {
        config = configFile.getConfig();
        guiEnabled = config.getBoolean("gui-enabled", false);
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public boolean isGuiEnabled() { return guiEnabled; }

    public String getTitleText() { return config.getString("gui.title"); }
    public String getCloseText() { return config.getString("gui.close"); }
    public String getPreviousText() { return config.getString("gui.previous"); }
    public String getNextText() { return config.getString("gui.next"); }
    public String getSpawnName() { return config.getString("gui.spawn-name"); }
    public String getWorldText() { return config.getString("gui.world"); }
    public String getXText() { return config.getString("gui.x"); }
    public String getYText() { return config.getString("gui.y"); }
    public String getZText() { return config.getString("gui.z"); }
    public String getErrorId() { return config.getString("gui.error-id"); }
}

