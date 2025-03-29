package managers;

import org.bukkit.configuration.file.FileConfiguration;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

public class VoidManager {
    private final ConfigFile configFile;

    private int id;
    private int layer;
    private boolean sendMessages;
    private boolean sendTitle;
    private boolean sendActionBar;

    public VoidManager(TSetSpawn plugin) {
        this.configFile = new ConfigFile("void.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = configFile.getConfig();

        id = config.getInt("void.id");
        layer = config.getInt("void.layer");
        sendMessages = config.getBoolean("options.send-messages");
        sendActionBar = config.getBoolean("options.send-actionbar");
        sendTitle = config.getBoolean("options.send-title");
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public boolean isSendTitle() {
        return sendTitle;
    }
    public boolean isSendActionBar() {
        return sendActionBar;
    }
    public boolean isSendMessages() {
        return sendMessages;
    }
    public int getId() {
        return id;
    }
    public int getLayer() {
        return layer;
    }
}
