package utils;

import managers.CooldownManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownHandler {
    private final CooldownManager cooldownManager;
    private final Map<UUID, Map<Integer, Double>> playerCooldowns = new HashMap<>();

    public CooldownHandler(CooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
    }

    public boolean canUse(@NotNull Player player, int spawnId) {
        UUID uuid = player.getUniqueId();
        double cooldownSeconds = cooldownManager.getCooldownFor(spawnId);
        if (cooldownSeconds <= 0) return true;

        double now = System.currentTimeMillis() / 1000.0;
        Map<Integer, Double> spawnTimes = playerCooldowns.getOrDefault(uuid, new HashMap<>());
        double lastUsed = spawnTimes.getOrDefault(spawnId, 0.0);

        return now - lastUsed >= cooldownSeconds;
    }

    public void updateLastUse(@NotNull Player player, int spawnId) {
        UUID uuid = player.getUniqueId();
        double now = System.currentTimeMillis() / 1000.0;
        playerCooldowns.computeIfAbsent(uuid, k -> new HashMap<>()).put(spawnId, now);
    }

    public double getRemaining(@NotNull Player player, int spawnId) {
        UUID uuid = player.getUniqueId();
        double cooldownSeconds = cooldownManager.getCooldownFor(spawnId);
        Map<Integer, Double> spawnTimes = playerCooldowns.get(uuid);
        if (spawnTimes == null) return 0;

        double lastUsed = playerCooldowns.getOrDefault(uuid, new HashMap<>()).getOrDefault(spawnId, 0.0);
        double remaining = cooldownSeconds - (System.currentTimeMillis() / 1000.0 - lastUsed);

        if (remaining <= 0) {
            spawnTimes.remove(spawnId);
            if (spawnTimes.isEmpty()) {
                playerCooldowns.remove(uuid);
            }
            return 0;
        }
        return remaining;
    }

    public void clear() {
        playerCooldowns.clear();
    }
}
