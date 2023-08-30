package minealex.tsetspawn.events;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import minealex.tsetspawn.TSetSpawn;

import java.util.ArrayList;
import java.util.List;

public class Welcome implements Listener {
    private List<String> newPlayers = new ArrayList<>(); // Lista para llevar un registro de los jugadores nuevos
    private TSetSpawn plugin;

    public Welcome(TSetSpawn plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        // Verificar si el jugador está en la lista de jugadores nuevos
        if (!newPlayers.contains(playerName)) {
            FileConfiguration config = plugin.getConfig();
            String gospawn = "Config.welcome";

            // Verificar si el mensaje de bienvenida está habilitado en la configuración
            if (config.getBoolean(gospawn)) {
                String mensajeBienvenida = config.getString("Config.welcome-message");

                // Reemplazar %player% con el nombre del jugador
                mensajeBienvenida = mensajeBienvenida.replace("%player%", playerName);

                // Enviar el mensaje de bienvenida
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', mensajeBienvenida));

                // Agregar al jugador a la lista de jugadores nuevos
                newPlayers.add(playerName);
            }
        }
    }
}
