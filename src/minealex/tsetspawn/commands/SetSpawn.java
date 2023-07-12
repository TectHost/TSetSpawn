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

public class SetSpawn implements CommandExecutor{
	
	private TSetSpawn plugin;
	
	public SetSpawn(TSetSpawn plugin) {
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
				Location l = jugador.getLocation();
				double X = l.getX();
				double Y = l.getY();
				double Z = l.getZ();
				String world = l.getWorld().getName();
				float yaw = l.getYaw();
				float pitch = l.getPitch();
				config.set("Config.Spawn.x", X);
				config.set("Config.Spawn.y", Y);
				config.set("Config.Spawn.z", Z);
				config.set("Config.Spawn.world", world);
				config.set("Config.Spawn.yaw", yaw);
				config.set("Config.Spawn.pitch", pitch);
				plugin.saveConfig();
				String spawncolocated = "Config.Translate.spawn-placed";
				jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(spawncolocated)));
				return true;
				}
	}
}