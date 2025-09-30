package listeners;

import managers.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;
import utils.DebugLogger;
import utils.TranslateColors;

import java.util.UUID;

public class JoinListener implements Listener {
    private final TSetSpawn plugin;
    private final SpawnsManager spawnsManager;
    private final DataManager dataManager;
    private final JoinManager joinManager;
    private final ConfigManager configManager;
    private final TranslateColors translateColors;
    private final MessagesManager messagesManager;

    private boolean joinedBefore;

    public JoinListener(@NotNull TSetSpawn plugin) {
        this.plugin = plugin;
        this.spawnsManager = plugin.getSpawnsManager();
        this.dataManager = plugin.getDataManager();
        this.joinManager = plugin.getJoinManager();
        this.configManager = plugin.getConfigManager();
        this.translateColors = plugin.getTranslateColors();
        this.messagesManager = plugin.getMessagesManager();
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        String playerName = player.getName();

        DebugLogger.log("Player joined:", playerName, "(", playerUUID, ")");

        int spawnId = getSpawnIdForPlayer(playerUUID);

        if (spawnId != -1) {
            DebugLogger.log("Teleporting player", playerName, "to spawn ID", spawnId);
            teleportPlayerToSpawn(event, spawnId);
        } else {
            DebugLogger.log("No spawn assigned for player: ", playerName);
        }

        if (configManager.isCheckUpdates()) checkUpdate(player);

        checkMessages(player, event);
    }

    private void checkMessages(Player player, PlayerJoinEvent event) {
        String message;
        if (joinedBefore) {
            message = joinManager.getJoin();
        } else {
            message = joinManager.getFirstJoin();
        }

        if (!message.isEmpty()) {
            event.joinMessage(translateColors.translateColors(player, message));
        }
    }

    private void checkUpdate(@NotNull Player player) {
        if (player.hasPermission("tss.admin.updates")) {
            String latestVersion = plugin.getLastVersion();
            String version = plugin.getDescription().getVersion();
            if (latestVersion != null && !latestVersion.equalsIgnoreCase(version)) {
                player.sendMessage(translateColors.translateColors(player,messagesManager.getUpdate().replace("%latestVersion%", latestVersion).replace("%version%", version)));
            }
        }
    }

    private int getSpawnIdForPlayer(UUID playerUUID) {
        joinedBefore = dataManager.hasJoinedBefore(playerUUID);

        DebugLogger.log("Checking join status for player:", playerUUID, "- Joined before?", joinedBefore);

        if (joinedBefore) {
            if (joinManager.isSpawnOnJoinEnabled()) {
                int id = joinManager.getSpawnOnJoinId();
                DebugLogger.log("Spawn-on-join is enabled. Using spawn ID:", id);
                return id;
            } else {
                DebugLogger.log("Spawn-on-join is disabled.");
                return -1;
            }
        } else {
            if (joinManager.isFirstJoinSpawnEnabled()) {
                int id = joinManager.getFirstJoinSpawnId();
                DebugLogger.log("First-join-spawn is enabled. Using spawn ID:", id);
                return id;
            } else {
                DebugLogger.log("First-join-spawn is disabled.");
                return -1;
            }
        }
    }

    private void teleportPlayerToSpawn(@NotNull PlayerJoinEvent event, int spawnId) {
        Location spawnLocation = spawnsManager.getSpawn(spawnId);
        String playerName = event.getPlayer().getName();

        if (spawnLocation != null) {
            event.getPlayer().teleport(spawnLocation);
            DebugLogger.log("Teleported player", playerName, "to spawn location:", spawnLocation);
            dataManager.setJoined(event.getPlayer().getUniqueId());
        } else {
            plugin.getLogger().warning("Spawn ID (" + spawnId + ") does not exist in spawns.yml!");
            DebugLogger.warn("Failed to teleport", playerName, "- spawn ID", spawnId, "not found.");
        }
    }
}
