package commands;

import managers.SpawnsManager;
import managers.SpawnMessagesManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;

public class Spawn implements CommandExecutor {
    private final TSetSpawn plugin;
    private final SpawnsManager spawnsManager;

    @Contract(pure = true)
    public Spawn(@NotNull TSetSpawn plugin) {
        this.plugin = plugin;
        this.spawnsManager = plugin.getSpawnsManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, plugin.getMessagesManager().getOnlyPlayer()));
            return true;
        }

        int spawnId = 0;

        if (args.length >= 2 && args[0].equalsIgnoreCase("--id")) {
            try {
                spawnId = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(plugin.getTranslateColors().translateColors(player, plugin.getMessagesManager().getInvalidSpawn()));
                return true;
            }
        }

        Location spawnLocation = spawnsManager.getSpawn(spawnId);
        if (spawnLocation == null) {
            player.sendMessage(plugin.getTranslateColors().translateColors(player, plugin.getMessagesManager().getNotExist().replace("%spawn%", String.valueOf(spawnId))));
            return true;
        }

        player.teleport(spawnLocation);
        String message = plugin.getSpawnMessagesManager().getSpawnMessage(spawnId);
        if (!message.isEmpty()) {
            player.sendMessage(plugin.getTranslateColors().translateColors(player, message));
        }
        return true;
    }
}
