package commands;

import managers.SpawnsManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;

public class SetSpawn implements CommandExecutor {
    private final TSetSpawn plugin;
    private final SpawnsManager spawnsManager;

    public SetSpawn(TSetSpawn plugin, SpawnsManager spawnsManager) {
        this.plugin = plugin;
        this.spawnsManager = spawnsManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, plugin.getMessagesManager().getOnlyPlayer()));
            return true;
        }

        if (!player.hasPermission("tss.admin") && !player.hasPermission("tss.setspawn")) {
            sender.sendMessage(plugin.getTranslateColors().translateColors(player, plugin.getMessagesManager().getNoPerm()));
            return true;
        }

        Location location = player.getLocation();
        int spawnId = 0;

        if (args.length > 0) {
            try {
                spawnId = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getTranslateColors().translateColors(player, plugin.getMessagesManager().getInvalidId()));
                return true;
            }
        }

        spawnsManager.setSpawn(spawnId, location, true);
        sender.sendMessage(plugin.getTranslateColors().translateColors(player, plugin.getMessagesManager().getSpawn().replace("%spawn%", String.valueOf(spawnId))));
        return true;
    }
}
