package utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tsetspawn.TSetSpawn;

import java.io.File;
import java.io.IOException;

public class ConfigFile {

    private final TSetSpawn plugin;
    private final String fileName;
    private final String folderName;
    private final boolean isYaml;

    private FileConfiguration fileConfiguration = null;
    private File file = null;

    public ConfigFile(String fileName, String folderName, TSetSpawn plugin) {
        this(fileName, folderName, plugin, true);
    }

    public ConfigFile(String fileName, String folderName, TSetSpawn plugin, boolean isYaml) {
        this.fileName = fileName;
        this.folderName = folderName;
        this.plugin = plugin;
        this.isYaml = isYaml;
    }

    public String getPath() {
        return this.fileName;
    }

    public void registerConfig() {
        if (folderName != null) {
            file = new File(plugin.getDataFolder() + File.separator + folderName, fileName);
        } else {
            file = new File(plugin.getDataFolder(), fileName);
        }

        if (!file.exists()) {
            if (folderName != null) {
                plugin.saveResource(folderName + File.separator + fileName, false);
            } else {
                plugin.saveResource(fileName, false);
            }
        }

        if (isYaml) {
            fileConfiguration = new YamlConfiguration();
            try {
                fileConfiguration.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                plugin.getLogger().severe("Error loading YAML file: " + fileName);
                e.printStackTrace();
            }
        }
    }

    public void saveConfig() {
        if (isYaml && fileConfiguration != null) {
            try {
                fileConfiguration.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public FileConfiguration getConfig() {
        if (!isYaml) throw new UnsupportedOperationException("This file is not a YAML file.");
        if (fileConfiguration == null) reloadConfig();
        return fileConfiguration;
    }

    public void reloadConfig() {
        if (!isYaml) throw new UnsupportedOperationException("This file is not a YAML file.");
        if (file == null) {
            if (folderName != null) {
                file = new File(plugin.getDataFolder() + File.separator + folderName, fileName);
            } else {
                file = new File(plugin.getDataFolder(), fileName);
            }
        }

        fileConfiguration = YamlConfiguration.loadConfiguration(file);
        if (file != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(file);
            fileConfiguration.setDefaults(defConfig);
        }
    }

    public File getFile() {
        return file;
    }
}
