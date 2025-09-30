package managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

import java.util.*;

public class BanManager {
    private final ConfigFile configFile;

    private Map<String, List<String>> bansPorSpawn;

    public BanManager(TSetSpawn plugin) {
        this.configFile = new ConfigFile("bans.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadBans();
    }

    private void loadBans() {
        bansPorSpawn = new HashMap<>();
        FileConfiguration config = configFile.getConfig();

        if (config.isConfigurationSection("bans")) {
            for (String spawnId : Objects.requireNonNull(config.getConfigurationSection("bans")).getKeys(false)) {
                List<String> list = config.getStringList("bans." + spawnId);
                bansPorSpawn.put(spawnId, new ArrayList<>(list));
            }
        }
    }

    public void reloadBans() {
        configFile.reloadConfig();
        loadBans();
    }

    public boolean banPlayer(@NotNull String spawnId, @NotNull UUID uuid) {
        List<String> list = bansPorSpawn.computeIfAbsent(spawnId, k -> new ArrayList<>());
        String uuidStr = uuid.toString();

        if (list.contains(uuidStr)) return false;
        list.add(uuidStr);
        saveBans();
        return true;
    }

    public boolean unbanPlayer(@NotNull String spawnId, @NotNull UUID uuid) {
        List<String> list = bansPorSpawn.get(spawnId);
        if (list == null) return false;
        boolean removed = list.remove(uuid.toString());
        if (removed) saveBans();
        return removed;
    }

    public boolean isBanned(@NotNull String spawnId, @NotNull UUID uuid) {
        List<String> list = bansPorSpawn.get(spawnId);
        return list != null && list.contains(uuid.toString());
    }

    public Set<UUID> getBannedPlayers(@NotNull String spawnId) {
        List<String> list = bansPorSpawn.get(spawnId);
        if (list == null) return Collections.emptySet();
        Set<UUID> uuids = new HashSet<>();
        for (String s : list) {
            try {
                uuids.add(UUID.fromString(s));
            } catch (IllegalArgumentException ignored) {}
        }
        return uuids;
    }

    public int clearBans(@NotNull String spawnId) {
        List<String> list = bansPorSpawn.remove(spawnId);
        if (list == null || list.isEmpty()) return 0;
        saveBans();
        return list.size();
    }

    private void saveBans() {
        FileConfiguration config = configFile.getConfig();
        config.set("bans", null);

        for (Map.Entry<String, List<String>> entry : bansPorSpawn.entrySet()) {
            config.set("bans." + entry.getKey(), entry.getValue());
        }

        configFile.saveConfig();
    }

    public void clearBans() {
        bansPorSpawn.clear();
    }
}
