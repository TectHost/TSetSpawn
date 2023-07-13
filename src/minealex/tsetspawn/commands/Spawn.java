package minealex.tsetspawn.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import minealex.tsetspawn.TSetSpawn;

public class Spawn implements CommandExecutor{
	
	private TSetSpawn plugin;
	
	public Spawn(TSetSpawn plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command comando, String label, String[] args) {
		if(!(sender instanceof Player)) {
			FileConfiguration config = plugin.getConfig();
			String console = "Config.Translate.console";
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(console)));
			return false;
		}else {
			Player jugador = (Player) sender;
			FileConfiguration config = plugin.getConfig();
			if(args.length > 0) {
				}
				if(config.contains("Config.Spawn.x")) {
					double x = Double.valueOf(config.getString("Config.Spawn.x"));
					double y = Double.valueOf(config.getString("Config.Spawn.y"));
					double z = Double.valueOf(config.getString("Config.Spawn.z"));
					float yaw = Float.valueOf(config.getString("Config.Spawn.yaw"));
					float pitch = Float.valueOf(config.getString("Config.Spawn.pitch"));
					World world = plugin.getServer().getWorld(config.getString("Config.Spawn.world"));
					Location l = new Location(world, x, y, z, yaw, pitch);
					jugador.teleport(l);
					String onspawn = "Config.Translate.spawn";
					jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(onspawn)));
					return true;
				}else {
					String spawnno = "Config.Translate.spawn-not-placed";
					jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(spawnno)));
					return true;
				}
			}
	}
}