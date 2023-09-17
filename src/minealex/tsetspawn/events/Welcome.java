package minealex.tsetspawn.events;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import minealex.tsetspawn.TSetSpawn;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Welcome implements Listener {
    private File playersFile;
    private YamlConfiguration playersConfig;
    private TSetSpawn plugin;

    public Welcome(TSetSpawn plugin) {
        this.plugin = plugin;

        // Initialize the players data file and configuration
        playersFile = new File(plugin.getDataFolder(), "players.yml");
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId(); // Get the player's UUID

        // Check if the player's UUID is in the data file
        if (playersConfig.contains(playerUUID.toString())) {
            // Player is not new, do not teleport to FTSpawn
            return;
        }

        FileConfiguration config = plugin.getConfig();
        boolean welcomeEnabled = config.getBoolean("Config.welcome");

        if (welcomeEnabled) {
            String mensajeBienvenida = config.getString("Config.welcome-message");
            mensajeBienvenida = mensajeBienvenida.replace("%player%", player.getName());

            // Send the welcome message
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', mensajeBienvenida));

            // Check if the first-time spawn is configured
            if (config.contains("Config.FTSpawn")) {
                // Delay the teleport to ensure everything is loaded
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    Location ftSpawn = getLocationFromConfig(config, "Config.FTSpawn");
                    if (ftSpawn != null) {
                        player.teleport(ftSpawn);
                    }
                }, 20L); // Delay of 1 second (20 ticks)
            }

            // Add the player's UUID to the data file
            playersConfig.set(playerUUID.toString(), true);
            savePlayersFile();
        }
    }
    
    private Location getLocationFromConfig(FileConfiguration config, String path) {
        if (config.contains(path + ".world") && config.contains(path + ".x") && config.contains(path + ".y")
                && config.contains(path + ".z") && config.contains(path + ".yaw") && config.contains(path + ".pitch")) {
            World world = plugin.getServer().getWorld(config.getString(path + ".world"));
            double x = config.getDouble(path + ".x");
            double y = config.getDouble(path + ".y");
            double z = config.getDouble(path + ".z");
            float yaw = (float) config.getDouble(path + ".yaw");
            float pitch = (float) config.getDouble(path + ".pitch");
            return new Location(world, x, y, z, yaw, pitch);
        }
        return null;
    }
    
    private void savePlayersFile() {
        try {
            playersConfig.save(playersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
