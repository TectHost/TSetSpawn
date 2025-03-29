package managers;

import org.bukkit.configuration.file.FileConfiguration;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

public class ConfigManager {
    private final ConfigFile configFile;

    private String langFile;
    private boolean join;
    private boolean messages;
    private boolean voidModule;
    private boolean titlesModule;
    private boolean actionBar;
    private boolean permissions;

    public ConfigManager(TSetSpawn plugin) {
        this.configFile = new ConfigFile("config.yml", null, plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = configFile.getConfig();

        langFile = config.getString("general.lang-file");

        join = config.getBoolean("modules.join", true);
        messages = config.getBoolean("modules.messages", true);
        voidModule = config.getBoolean("modules.void", true);
        titlesModule = config.getBoolean("modules.titles", true);
        actionBar = config.getBoolean("modules.actionbar", true);
        permissions = config.getBoolean("modules.permissions", true);
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public boolean isPermissions() {
        return permissions;
    }
    public boolean isActionBar() {
        return actionBar;
    }
    public boolean isTitlesModule() {
        return titlesModule;
    }
    public boolean isVoidModule() {
        return voidModule;
    }
    public String getLangFile() {
        return langFile;
    }
    public boolean isJoin() {
        return join;
    }
    public boolean isMessages() {
        return messages;
    }
}
