package commands;

import managers.MessagesManager;
import managers.SpawnsManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;
import utils.TranslateColors;

public class SetSpawn implements CommandExecutor {
    private final SpawnsManager spawnsManager;
    private final TranslateColors translateColors;
    private final MessagesManager messagesManager;

    @Contract(pure = true)
    public SetSpawn(@NotNull TSetSpawn plugin, SpawnsManager spawnsManager) {
        this.spawnsManager = spawnsManager;
        this.translateColors = plugin.getTranslateColors();
        this.messagesManager = plugin.getMessagesManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(translateColors.translateColors(null, messagesManager.getOnlyPlayer()));
            return true;
        }

        if (!player.hasPermission("tss.admin") && !player.hasPermission("tss.setspawn")) {
            sender.sendMessage(translateColors.translateColors(player, messagesManager.getNoPerm()));
            return true;
        }

        Location location = player.getLocation();
        int spawnId = 0;

        if (args.length > 0) {
            try {
                spawnId = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(translateColors.translateColors(player, messagesManager.getInvalidId()));
                return true;
            }
        }

        spawnsManager.setSpawn(spawnId, location, true);
        sender.sendMessage(translateColors.translateColors(player, messagesManager.getSpawn().replace("%spawn%", String.valueOf(spawnId))));
        return true;
    }
}
