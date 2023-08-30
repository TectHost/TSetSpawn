package minealex.tsetspawn.events;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import minealex.tsetspawn.TSetSpawn;

public class Enter implements Listener {
    private TSetSpawn plugin;

    public Enter(TSetSpawn plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntering(PlayerJoinEvent event) {
        Player jugador = event.getPlayer();
        FileConfiguration config = plugin.getConfig();
        String gospawn = "Config.go-spawn";

        if (config.getString(gospawn).equals("true")) {
            if (config.contains("Config.Spawn.x") && !hasPlayerTeleported(jugador)) {
                double x = Double.valueOf(config.getString("Config.Spawn.x"));
                double y = Double.valueOf(config.getString("Config.Spawn.y"));
                double z = Double.valueOf(config.getString("Config.Spawn.z"));
                float yaw = Float.valueOf(config.getString("Config.Spawn.yaw"));
                float pitch = Float.valueOf(config.getString("Config.Spawn.pitch"));
                World world = plugin.getServer().getWorld(config.getString("Config.Spawn.world"));
                Location l = new Location(world, x, y, z, yaw, pitch);
                jugador.teleport(l);

                // Mark the player as having teleported to the regular spawn
                markPlayerAsTeleported(jugador);
            }
        }

        String path = "Config.enter-message";
        if (config.getString(path).equals("true")) {
            String texto = "Config.enter-message-text";
            jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(texto)).replaceAll("%player%", jugador.getName()));
        }
    }

    private boolean hasPlayerTeleported(Player player) {
        // You can implement logic here to check if the player has teleported
        // to the regular spawn before
        // For simplicity, let's assume that players haven't teleported before.
        return false;
    }

    private void markPlayerAsTeleported(Player player) {
        // Implement logic to mark the player as having teleported before
        // You could use a database, player data, or a persistent file for this
        // For simplicity, we won't implement it here.
    }
}
