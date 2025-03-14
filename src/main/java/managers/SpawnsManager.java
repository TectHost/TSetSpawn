package managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SpawnsManager {
    private final ConfigFile configFile;
    private final Map<Integer, Location> spawns;
    private Location spawn0;

    public SpawnsManager(TSetSpawn plugin) {
        this.configFile = new ConfigFile("spawns.yml", null, plugin);
        this.configFile.registerConfig();
        this.spawns = new HashMap<>();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = configFile.getConfig();
        clearSpawns();
        spawn0 = null;

        if (config.contains("spawns")) {
            if (config.contains("spawns.0")) {
                spawn0 = loadLocation(config, "spawns.0");
            }

            if (config.getConfigurationSection("spawns") != null) {
                for (String key : Objects.requireNonNull(config.getConfigurationSection("spawns")).getKeys(false)) {
                    int id = Integer.parseInt(key);
                    if (id != 0) {
                        spawns.put(id, loadLocation(config, "spawns." + id));
                    }
                }
            }
        }
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public void setSpawn(int id, Location location, boolean save) {
        if (id == 0) {
            spawn0 = location;
        } else {
            spawns.put(id, location);
        }

        if (save) {
            saveSpawn(id, location);
        }
    }

    public Location getSpawn(int id) {
        return id == 0 ? spawn0 : spawns.get(id);
    }

    private void saveSpawn(int id, Location location) {
        FileConfiguration config = configFile.getConfig();
        saveLocation(config, "spawns." + id, location);
        configFile.saveConfig();
    }

    private @Nullable Location loadLocation(@NotNull FileConfiguration config, String path) {
        if (!config.contains(path)) return null;
        return new Location(
                Bukkit.getWorld(Objects.requireNonNull(config.getString(path + ".world"))),
                config.getDouble(path + ".x"),
                config.getDouble(path + ".y"),
                config.getDouble(path + ".z"),
                (float) config.getDouble(path + ".yaw"),
                (float) config.getDouble(path + ".pitch")
        );
    }

    private void saveLocation(@NotNull FileConfiguration config, String path, @NotNull Location location) {
        config.set(path + ".world", location.getWorld().getName());
        config.set(path + ".x", location.getX());
        config.set(path + ".y", location.getY());
        config.set(path + ".z", location.getZ());
        config.set(path + ".yaw", location.getYaw());
        config.set(path + ".pitch", location.getPitch());
    }

    public void clearSpawns() {
        spawns.clear();
    }
}
