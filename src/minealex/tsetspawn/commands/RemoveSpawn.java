package minealex.tsetspawn.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import minealex.tsetspawn.TSetSpawn;
import net.md_5.bungee.api.ChatColor;

public class RemoveSpawn implements CommandExecutor {

    private TSetSpawn plugin;

    public RemoveSpawn(TSetSpawn plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Config.Translate.console")));
            return true;
        }

        Player player = (Player) sender;
        FileConfiguration config = plugin.getConfig();

        if (!player.hasPermission("tsetspawn.remove")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("Config.Translate.no-permission")));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("Config.Translate.remove-usage")));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("spawn")) {
            config.set("Config.Spawn", null);
            plugin.saveConfig();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("Config.Translate.spawn-removed")));
        } else if (subCommand.equals("ftspawn")) {
            config.set("Config.FTSpawn", null);
            plugin.saveConfig();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("Config.Translate.ftspawn-removed")));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("Config.Translate.remove-usage")));
        }

        return true;
    }
}
