package managers;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.FireworkEffect.Type;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;
import utils.FireworksHandler;

import java.util.*;

public class FireworksManager {
    private final TSetSpawn plugin;
    private final ConfigFile configFile;
    private final Map<Integer, FireworkEffect> fireworkEffects;

    public FireworksManager(TSetSpawn plugin) {
        this.plugin = plugin;
        this.configFile = new ConfigFile("fireworks.yml", "modules", plugin);
        this.configFile.registerConfig();
        this.fireworkEffects = new HashMap<>();
        loadFireworks();
    }

    private  void loadFireworks() {
        FileConfiguration config = configFile.getConfig();
        clear();

        for (String key : Objects.requireNonNull(config.getConfigurationSection("fireworks")).getKeys(false)) {
            try {
                int id = Integer.parseInt(key);
                String path = "fireworks." + id;

                List<String> colorsRaw = config.getStringList(path + ".colors");
                List<String> fadesRaw = config.getStringList(path + ".fadeColors");
                boolean flicker = config.getBoolean(path + ".flicker", false);
                boolean trail = config.getBoolean(path + ".trail", false);
                String typeRaw = config.getString(path + ".type", "BALL");

                List<Color> colors = FireworksHandler.parseColorList(colorsRaw, plugin);
                List<Color> fadeColors = FireworksHandler.parseColorList(fadesRaw, plugin);
                Type type = FireworksHandler.parseType(typeRaw, plugin);

                FireworkEffect effect = FireworkEffect.builder()
                        .withColor(colors)
                        .withFade(fadeColors)
                        .flicker(flicker)
                        .trail(trail)
                        .with(type)
                        .build();

                fireworkEffects.put(id, effect);

            } catch (Exception e) {
                plugin.getLogger().warning("Error loading firework ID " + key + ": " + e.getMessage());
            }
        }
    }

    public FireworkEffect getEffect(int spawnId) {
        return fireworkEffects.get(spawnId);
    }

    public void reloadFireworks() {
        configFile.reloadConfig();
        loadFireworks();
    }

    public void clear() {
        fireworkEffects.clear();
    }
}
