package minealex.tsetspawn.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import minealex.tsetspawn.TSetSpawn;

public class FTSetSpawn implements CommandExecutor {

    private TSetSpawn plugin;

    public FTSetSpawn(TSetSpawn plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command comando, String label, String[] args) {
        if (!(sender instanceof Player)) {
            FileConfiguration config = plugin.getConfig();
            String console = "Config.Translate.console";
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(console)));
            return false;
        } else {
            Player jugador = (Player) sender;
            FileConfiguration config = plugin.getConfig();
            if (args.length > 0) {
                // Manejar los argumentos del comando aquí
            }

            if (!jugador.hasPermission("tsetspawn.ftsetspawn")) {
                String noPermissionMessage = config.getString("Config.Translate.no-permission", "&5TSetSpawn &e> &cYou do not have permissions");
                jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', noPermissionMessage));
                return true;
            }

            // Resto del código para establecer el punto de spawn
            Location l = jugador.getLocation();
            double X = l.getX();
            double Y = l.getY();
            double Z = l.getZ();
            String world = l.getWorld().getName();
            float yaw = l.getYaw();
            float pitch = l.getPitch();
            config.set("Config.FTSpawn.x", X);
            config.set("Config.FTSpawn.y", Y);
            config.set("Config.FTSpawn.z", Z);
            config.set("Config.FTSpawn.world", world);
            config.set("Config.FTSpawn.yaw", yaw);
            config.set("Config.FTSpawn.pitch", pitch);
            plugin.saveConfig();
            String spawncolocated = "Config.Translate.spawn-placed";
            jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(spawncolocated)));
            return true;
        }
    }
}
