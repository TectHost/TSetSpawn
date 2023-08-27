package minealex.tsetspawn.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import minealex.tsetspawn.TSetSpawn;

public class Commands implements CommandExecutor{
	
	private TSetSpawn plugin;
	
	public Commands(TSetSpawn plugin) {
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
			if (args.length > 0) {
			    if (args[0].equalsIgnoreCase("version")) {
			        String version = "Config.Translate.version";
			        if (jugador.hasPermission("tsetspawn.version")) {
			            jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(version)).replaceAll("%version%", plugin.version));
			            return true;
			        } else {
			        	String noperms = "Config.Translate.no-permission";
			        	jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(noperms)));
			            return true;
			        }
			    }
			    else if (args[0].equalsIgnoreCase("reload")) {
			        if (jugador.hasPermission("tsetspawn.reload")) {
			            plugin.reloadConfig();
			            plugin.getConfig();
			            String reload = "Config.Translate.reload";
			            jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(reload)));
			            return true;
			        } else {
			            String noperms = "Config.Translate.no-permission";
			            jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(noperms)));
			            return true;
			        }
			    }
				else {
					String unknown = "Config.Translate.unknown-command";
					jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(unknown)));
				}
			}else {
				String help = "Config.Translate.help";
				jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(help)));
				return true;
			}
		}
		return false;
	}

}