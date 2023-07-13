package minealex.tsetspawn.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import minealex.tsetspawn.TSetSpawn;

public class Welcome implements Listener {
    private boolean primerIngreso;
    private TSetSpawn plugin;
	
    public Welcome(TSetSpawn plugin) {
        this.plugin = plugin;
    }
	
    public void onEnable() {
        primerIngreso = true;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (primerIngreso) {
            FileConfiguration config = plugin.getConfig();
            Player jugador = event.getPlayer();
            String gospawn = "Config.welcome";
            if (config.getString(gospawn).equals("true")) {
                String texto = "Config.welcome-message";
                broadcastMessage(ChatColor.translateAlternateColorCodes('&', config.getString(texto)).replaceAll("%player%", jugador.getName()));
                primerIngreso = false;
            }
        }
    }

    private void broadcastMessage(String message) {
        for (Player jugador : Bukkit.getServer().getOnlinePlayers()) {
            jugador.sendMessage(message);
        }
    }
}
