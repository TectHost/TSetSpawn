package listeners;

import commands.Spawn;
import managers.ConfigManager;
import managers.MessagesManager;
import managers.RespawnManager;
import managers.SpawnsManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;
import utils.DebugLogger;
import utils.TranslateColors;

public class PlayerRespawnListener implements Listener {

    private final TSetSpawn plugin;
    private final Spawn spawnCommand;
    private final ConfigManager configManager;
    private final RespawnManager respawnManager;
    private final SpawnsManager spawnsManager;
    private final TranslateColors translateColors;
    private final MessagesManager messagesManager;

    public PlayerRespawnListener(@NotNull TSetSpawn plugin, @NotNull Spawn spawnCommand) {
        this.plugin = plugin;
        this.spawnCommand = spawnCommand;
        this.configManager = plugin.getConfigManager();
        this.respawnManager = plugin.getRespawnManager();
        this.spawnsManager = plugin.getSpawnsManager();
        this.translateColors = plugin.getTranslateColors();
        this.messagesManager = plugin.getMessagesManager();
    }

    @EventHandler
    public void onPlayerRespawn(@NotNull PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        final int spawnId = respawnManager.getId();
        final Location location = spawnsManager.getSpawn(spawnId);

        DebugLogger.log("Player", player.getName(), "has respawned. Using spawn ID:", spawnId);

        if (location == null) {
            DebugLogger.warn("Respawn failed: Spawn ID", spawnId, "does not exist.");
            player.sendMessage(translateColors.translateColors(player,
                    messagesManager.getNotExist().replace("%spawn%", String.valueOf(spawnId))));
            return;
        }

        event.setRespawnLocation(location);
        DebugLogger.log("Set respawn location for", player.getName(), "to spawn ID", spawnId);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) return;
            DebugLogger.log("Applying respawn effects to", player.getName());
            applyEffects(player, spawnId);
        }, 2L);
    }

    private void applyEffects(@NotNull Player player, int spawnId) {
        final String name = player.getName();

        if (configManager.isMessages()) {
            DebugLogger.log("Sending message to", name);
            spawnCommand.sendMessage(player, spawnId);
        }
        if (configManager.isTitlesModule()) {
            DebugLogger.log("Sending title to", name);
            spawnCommand.sendTitle(player, spawnId);
        }
        if (configManager.isActionBar()) {
            DebugLogger.log("Sending action bar to", name);
            spawnCommand.sendActionBar(player, spawnId);
        }
        if (configManager.isSounds()) {
            DebugLogger.log("Playing sound for", name);
            spawnCommand.sendSound(player, spawnId);
        }
        if (configManager.isParticles()) {
            DebugLogger.log("Spawning particles for", name);
            spawnCommand.sendParticle(player, spawnId);
        }
        if (configManager.isFireworks()) {
            DebugLogger.log("Launching firework for", name);
            spawnCommand.sendFirework(player, spawnId);
        }
    }
}
