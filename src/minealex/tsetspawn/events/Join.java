package minealex.tsetspawn.events;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import minealex.tsetspawn.TSetSpawn;

public class Join implements Listener {
    
    private TSetSpawn plugin;
    
    public Join(TSetSpawn plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        FileConfiguration config = plugin.getConfig();
        boolean join = config.getBoolean("Config.join");
        if(join) {
            String joinmessage = "Config.join-message";
            Player jugador = event.getPlayer();
            String mensaje = ChatColor.translateAlternateColorCodes('&', config.getString(joinmessage)).replaceAll("%player%", jugador.getName());
            
            event.setJoinMessage(mensaje);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        FileConfiguration config = plugin.getConfig();
        boolean quit = config.getBoolean("Config.quit");
        if(quit) {
            String quitmessage = "Config.quit-message";
            Player jugador = event.getPlayer();
            String mensaje = ChatColor.translateAlternateColorCodes('&', config.getString(quitmessage)).replaceAll("%player%", jugador.getName());
        
            event.setQuitMessage(mensaje);
        }
    }
}
