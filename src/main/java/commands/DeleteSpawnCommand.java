package commands;

import managers.*;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;
import utils.DebugLogger;
import utils.TranslateColors;
import utils.Utils;

public class DeleteSpawnCommand implements CommandExecutor {

    private final SpawnsManager spawnsManager;
    private final TranslateColors translateColors;
    private final MessagesManager messagesManager;
    private final Utils utils;

    public DeleteSpawnCommand(@NotNull TSetSpawn plugin) {
        this.spawnsManager = plugin.getSpawnsManager();
        this.translateColors = plugin.getTranslateColors();
        this.messagesManager = plugin.getMessagesManager();
        this.utils = plugin.getUtils();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull[] args) {
        DebugLogger.log("Executing /deletespawn command. Args:", args);

        if (!utils.hasPerm(sender, "tss.admin.delete")) {
            return true;
        }

        if (args.length == 0) {
            DebugLogger.log("No arguments provided for /deletespawn.");
            sender.sendMessage(translateColors.translateColors(null, messagesManager.getInvalidSpawn()));
            return true;
        }

        if (args[0].equalsIgnoreCase("all")) {
            DebugLogger.log("Deleting all spawns.");
            deleteSpawn(sender, -1, true);
            return true;
        }

        int spawnId;
        try {
            spawnId = Integer.parseInt(args[0]);
            DebugLogger.log("Parsed spawn ID:", spawnId);
        } catch (NumberFormatException e) {
            DebugLogger.error("Invalid spawn ID format: " + args[0], e);
            sender.sendMessage(translateColors.translateColors(null, messagesManager.getInvalidSpawn()));
            return true;
        }

        deleteSpawn(sender, spawnId, false);
        return true;
    }

    private void deleteSpawn(CommandSender sender, int spawnId, boolean deleteAll) {
        if (deleteAll) {
            spawnsManager.clearSpawns();
            spawnsManager.deleteAllSpawnsFromFile();
            sender.sendMessage(translateColors.translateColors(null, messagesManager.getAllSpawnsDeleted()));
            DebugLogger.log("All spawns deleted successfully.");
            return;
        }

        Location existing = spawnsManager.getSpawn(spawnId);

        if (existing == null) {
            DebugLogger.log("Spawn ID", spawnId, "does not exist. Cannot delete.");
            sender.sendMessage(translateColors.translateColors(null, messagesManager.getNotExist().replace("%spawn%", String.valueOf(spawnId))));
            return;
        }

        spawnsManager.deleteSpawn(spawnId);
        sender.sendMessage(translateColors.translateColors(null, messagesManager.getSpawnDeleted().replace("%spawn%", String.valueOf(spawnId))));
        DebugLogger.log("Spawn ID", spawnId, "deleted successfully.");
    }
}
