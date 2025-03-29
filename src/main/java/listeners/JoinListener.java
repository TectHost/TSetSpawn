package listeners;

import managers.DataManager;
import managers.JoinManager;
import managers.SpawnsManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;

import java.util.UUID;

public class JoinListener implements Listener {
    private final TSetSpawn plugin;
    private final SpawnsManager spawnsManager;
    private final DataManager dataManager;
    private final JoinManager joinManager;

    public JoinListener(@NotNull TSetSpawn plugin) {
        this.plugin = plugin;
        this.spawnsManager = plugin.getSpawnsManager();
        this.dataManager = plugin.getDataManager();
        this.joinManager = plugin.getJoinManager();
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        int spawnId = getSpawnIdForPlayer(playerUUID);

        if (spawnId != -1) {
            teleportPlayerToSpawn(event, spawnId);
        }
    }

    private int getSpawnIdForPlayer(UUID playerUUID) {
        if (dataManager.hasJoinedBefore(playerUUID)) {
            if (joinManager.isSpawnOnJoinEnabled()) {
                return joinManager.getSpawnOnJoinId();
            } else {
                return -1;
            }
        } else {
            if (joinManager.isFirstJoinSpawnEnabled()) {
                return joinManager.getFirstJoinSpawnId();
            } else {
                return -1;
            }
        }
    }

    private void teleportPlayerToSpawn(PlayerJoinEvent event, int spawnId) {
        Location spawnLocation = spawnsManager.getSpawn(spawnId);
        if (spawnLocation != null) {
            event.getPlayer().teleport(spawnLocation);
            dataManager.setJoined(event.getPlayer().getUniqueId());
        } else {
            plugin.getLogger().warning("Spawn ID (" + spawnId + ") does not exist in spawns.yml!");
        }
    }
}
