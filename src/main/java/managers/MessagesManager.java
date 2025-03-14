package managers;

import org.bukkit.configuration.file.FileConfiguration;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

public class MessagesManager {

    private ConfigFile messagesFile;
    private final TSetSpawn plugin;

    private String onlyPlayer;
    private String invalidSpawn;
    private String notExist;
    private String invalidId;
    private String spawn;
    private String reload;
    private String version;
    private String noPerm;

    public MessagesManager(TSetSpawn plugin){
        this.messagesFile = new ConfigFile(plugin.getConfigManager().getLangFile(), "lang", plugin);
        this.plugin = plugin;
        this.messagesFile.registerConfig();
        loadConfig();
        generateAdditionalFiles();
    }

    public void loadConfig(){
        FileConfiguration config = messagesFile.getConfig();

        onlyPlayer = config.getString("messages.only-player");
        invalidSpawn = config.getString("messages.invalid-spawn");
        notExist = config.getString("messages.not-exist");
        invalidId = config.getString("messages.invalid-id");
        spawn = config.getString("messages.spawn");
        reload = config.getString("messages.reload");
        version = config.getString("messages.version");
        noPerm = config.getString("messages.no-perm");
    }

    public void reloadConfig(){
        this.messagesFile = new ConfigFile(plugin.getConfigManager().getLangFile(), "lang", plugin);
        messagesFile.reloadConfig();
        loadConfig();
    }

    public void generateAdditionalFiles() {
        createConfigFile("messages_es.yml");
        createConfigFile("messages_en.yml");
    }

    private void createConfigFile(String fileName) {
        ConfigFile configFile = new ConfigFile(fileName, "lang", plugin);
        configFile.registerConfig();
    }

    public String getNoPerm() {
        return noPerm;
    }
    public String getReload() {
        return reload;
    }
    public String getVersion() {
        return version;
    }
    public String getSpawn() {
        return spawn;
    }
    public String getOnlyPlayer() {
        return onlyPlayer;
    }
    public String getInvalidSpawn() {
        return invalidSpawn;
    }
    public String getNotExist() {
        return notExist;
    }
    public String getInvalidId() {
        return invalidId;
    }
}