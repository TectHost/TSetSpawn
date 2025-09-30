package commands;

import managers.ConfigManager;
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
import utils.DebugLogger;
import utils.TranslateColors;
import utils.Utils;

public class SetSpawn implements CommandExecutor {

    private final SpawnsManager spawnsManager;
    private final TranslateColors translateColors;
    private final MessagesManager messagesManager;
    private final Utils utils;
    private final ConfigManager configManager;

    @Contract(pure = true)
    public SetSpawn(@NotNull TSetSpawn plugin, SpawnsManager spawnsManager) {
        this.spawnsManager = spawnsManager;
        this.translateColors = plugin.getTranslateColors();
        this.messagesManager = plugin.getMessagesManager();
        this.utils = plugin.getUtils();
        this.configManager = plugin.getConfigManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull[] args) {
        DebugLogger.log("Executing /setspawn command. Args:", args);

        Player player = utils.getPlayerIfExists(sender);
        if (player == null) return true;

        if (!utils.hasPerm(player, "tss.setspawn")) return true;

        if (configManager.isSafeLocation() && !Utils.isSafeLocation(player.getLocation())) {
            DebugLogger.log("The location is unsafe");
            sender.sendMessage(translateColors.translateColors(player, messagesManager.getSafeLocation()));
            return true;
        }

        int spawnId = 0;
        boolean confirmed = false;

        if (args.length > 0) {
            try {
                spawnId = Integer.parseInt(args[0]);
                DebugLogger.log("Parsed spawn ID:", spawnId);
            } catch (NumberFormatException e) {
                DebugLogger.error("Invalid spawn ID format: " + args[0], e);
                sender.sendMessage(translateColors.translateColors(player, messagesManager.getInvalidId()));
                return true;
            }

            if (args.length > 1 && args[1].equalsIgnoreCase("confirm")) {
                confirmed = true;
                DebugLogger.log("Confirmation detected for overwriting spawn ID", spawnId);
            }
        }

        if (spawnsManager.getSpawn(spawnId) != null && !confirmed) {
            DebugLogger.log("Spawn ID", spawnId, "already exists. No confirmation provided.");
            sender.sendMessage(translateColors.translateColors(player, messagesManager.getAlreadyExists().replace("%spawn%", String.valueOf(spawnId))));
            return true;
        }

        Location location = player.getLocation();
        DebugLogger.log("Setting spawn ID", spawnId, "at location:", location);

        spawnsManager.setSpawn(spawnId, location, true);

        sender.sendMessage(translateColors.translateColors(player, messagesManager.getSpawn().replace("%spawn%", String.valueOf(spawnId))));
        DebugLogger.log("Spawn ID", spawnId, "successfully set.");

        return true;
    }
}
