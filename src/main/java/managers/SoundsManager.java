package managers;

import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SoundsManager {
    private final TSetSpawn plugin;
    private final ConfigFile configFile;
    private final Map<Integer, Sound> spawnSounds;

    public SoundsManager(TSetSpawn plugin) {
        this.plugin = plugin;
        this.configFile = new ConfigFile("sounds.yml", "modules", plugin);
        this.configFile.registerConfig();
        this.spawnSounds = new HashMap<>();
        loadSounds();
    }

    public void loadSounds() {
        FileConfiguration config = configFile.getConfig();
        clearSounds();

        for (String key : Objects.requireNonNull(config.getConfigurationSection("sounds")).getKeys(false)) {
            try {
                int id = Integer.parseInt(key);
                String soundName = config.getString("sounds." + id);

                if (soundName != null && !soundName.isEmpty()) {
                    try {
                        Sound sound = Sound.valueOf(soundName.toUpperCase());
                        spawnSounds.put(id, sound);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid sound name for ID " + id + ": " + soundName);
                    }
                } else {
                    plugin.getLogger().warning("Empty or null sound for ID: " + id);
                }
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Invalid sound ID (not a number): " + key);
            }
        }
    }

    public Sound getSpawnSound(int spawnId) {
        return spawnSounds.get(spawnId);
    }

    public void reloadSounds() {
        configFile.reloadConfig();
        clearSounds();
        loadSounds();
    }

    public void clearSounds() {
        spawnSounds.clear();
    }
}
