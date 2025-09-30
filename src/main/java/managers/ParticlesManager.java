package managers;

import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ParticlesManager {
    private final TSetSpawn plugin;
    private final ConfigFile configFile;
    private final Map<Integer, Particle> spawnParticles;

    public ParticlesManager(TSetSpawn plugin) {
        this.plugin = plugin;
        this.configFile = new ConfigFile("particles.yml", "modules", plugin);
        this.configFile.registerConfig();
        this.spawnParticles = new HashMap<>();
        loadParticles();
    }

    public void loadParticles() {
        FileConfiguration config = configFile.getConfig();
        clearParticles();

        for (String key : Objects.requireNonNull(config.getConfigurationSection("particles")).getKeys(false)) {
            try {
                int id = Integer.parseInt(key);
                String particleName = config.getString("particles." + id);

                if (particleName != null && !particleName.isEmpty()) {
                    try {
                        Particle particle = Particle.valueOf(particleName.toUpperCase());
                        spawnParticles.put(id, particle);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid particle for ID " + id + ": " + particleName);
                    }
                } else {
                    plugin.getLogger().warning("Empty or null particle for ID: " + id);
                }
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Invalid particle ID (not a number): " + key);
            }
        }
    }

    public Particle getSpawnParticle(int spawnId) {
        return spawnParticles.get(spawnId);
    }

    public void reloadParticles() {
        configFile.reloadConfig();
        clearParticles();
        loadParticles();
    }

    public void clearParticles() {
        spawnParticles.clear();
    }
}
