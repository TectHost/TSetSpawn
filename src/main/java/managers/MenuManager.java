package managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

public class MenuManager {

    private final ConfigFile configFile;

    private ConfigurationSection modulesSection;
    private String menuTitle;
    private String closeMessage;
    private String closeButtonName;
    private String noModulesMessage;

    public MenuManager(TSetSpawn plugin) {
        this.configFile = new ConfigFile("gui.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfiguration();
    }

    private void loadConfiguration() {
        FileConfiguration config = configFile.getConfig();

        modulesSection = config.getConfigurationSection("modules");
        menuTitle = config.getString("menu.title");
        closeMessage = config.getString("menu.close-message");
        closeButtonName = config.getString("menu.close-button");
        noModulesMessage = config.getString("menu.no-modules");
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfiguration();
    }

    public ConfigurationSection getModulesSection() {
        return modulesSection;
    }

    public String getMenuTitle() {
        return menuTitle;
    }

    public String getCloseMessage() {
        return closeMessage;
    }

    public String getCloseButtonName() {
        return closeButtonName;
    }

    public String getNoModulesMessage() {
        return noModulesMessage;
    }
}
