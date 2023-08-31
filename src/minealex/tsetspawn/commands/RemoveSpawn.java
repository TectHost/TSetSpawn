package minealex.tsetspawn.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import minealex.tsetspawn.TSetSpawn;

public class RemoveSpawn implements CommandExecutor {

    private TSetSpawn plugin;

    public RemoveSpawn(TSetSpawn plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        Player player = (Player) sender;
        FileConfiguration config = plugin.getConfig();

        if (args.length == 0) {
            player.sendMessage("Usage: /removespawn <spawn|ftspawn>");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("spawn")) {
            config.set("Config.Spawn", null);
            plugin.saveConfig();
            player.sendMessage("Spawn removed from config.yml.");
        } else if (subCommand.equals("ftspawn")) {
            config.set("Config.FTSpawn", null);
            plugin.saveConfig();
            player.sendMessage("FTSpawn removed from config.yml.");
        } else {
            player.sendMessage("Usage: /removespawn <spawn|ftspawn>");
        }

        return true;
    }
}
