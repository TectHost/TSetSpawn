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

public class PlayerMoveListener implements Listener {
    private final TSetSpawn plugin;
    private final VoidManager voidManager;
    private final Spawn spawnCommand;

    @Contract(pure = true)
    public PlayerMoveListener(@NotNull TSetSpawn plugin, Spawn spawnCommand) {
        this.plugin = plugin;
        this.voidManager = plugin.getVoidManager();
        this.spawnCommand = spawnCommand;
    }

    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getLocation().getY() > voidManager.getLayer()) {
            return;
        }

        int id = voidManager.getId();
        Location spawn = plugin.getSpawnsManager().getSpawn(id);

        if (spawn == null) {
            return;
        }

        player.teleport(spawn);

        // Options
        if (voidManager.isSendMessages()) spawnCommand.sendMessage(player, id);
        if (voidManager.isSendTitle()) spawnCommand.sendTitle(player, id);
        if (voidManager.isSendActionBar()) spawnCommand.sendActionBar(player, id);
    }
}
