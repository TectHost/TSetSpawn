package listeners;

import commands.Spawn;
import managers.VoidManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;
import utils.DebugLogger;

public class PlayerMoveListener implements Listener {
    private final TSetSpawn plugin;
    private final VoidManager voidManager;
    private final Spawn spawnCommand;

    @Contract(pure = true)
    public PlayerMoveListener(@NotNull TSetSpawn plugin, @NotNull Spawn spawnCommand) {
        this.plugin = plugin;
        this.voidManager = plugin.getVoidManager();
        this.spawnCommand = spawnCommand;
    }

    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final double y = player.getLocation().getY();
        final int voidLayer = voidManager.getLayer();

        if (y > voidLayer) return;

        DebugLogger.log("Player", player.getName(), "below void layer (Y =", y, ", limit =", voidLayer, ")");

        final int spawnId = voidManager.getId();
        final Location spawnLocation = plugin.getSpawnsManager().getSpawn(spawnId);

        if (spawnLocation == null) {
            DebugLogger.warn("Void teleport failed: Spawn ID", spawnId, "is not defined.");
            return;
        }

        player.teleport(spawnLocation);
        DebugLogger.log("Teleported", player.getName(), "to spawn ID", spawnId, "after falling into the void.");

        sendEffects(player, spawnId);
    }

    private void sendEffects(@NotNull Player player, int spawnId) {
        final String name = player.getName();

        if (voidManager.isSendMessages()) {
            DebugLogger.log("Sending message to", name);
            spawnCommand.sendMessage(player, spawnId);
        }
        if (voidManager.isSendTitle()) {
            DebugLogger.log("Sending title to", name);
            spawnCommand.sendTitle(player, spawnId);
        }
        if (voidManager.isSendActionBar()) {
            DebugLogger.log("Sending action bar to", name);
            spawnCommand.sendActionBar(player, spawnId);
        }
        if (voidManager.isSendSound()) {
            DebugLogger.log("Playing sound for", name);
            spawnCommand.sendSound(player, spawnId);
        }
        if (voidManager.isSendParticle()) {
            DebugLogger.log("Spawning particles for", name);
            spawnCommand.sendParticle(player, spawnId);
        }
    }
}
