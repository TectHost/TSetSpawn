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
    private boolean sendSound;
    private boolean sendParticle;
    private boolean sendFirework;

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
        sendSound = config.getBoolean("options.send-sound");
        sendParticle = config.getBoolean("options.send-particle");
        sendFirework = config.getBoolean("options.send-firework");
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
    public boolean isSendSound() {
        return sendSound;
    }
    public boolean isSendParticle() {
        return sendParticle;
    }
    public boolean isSendFirework() {
        return sendFirework;
    }
}
