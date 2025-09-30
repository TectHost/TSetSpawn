package commands;

import managers.BanManager;
import managers.MessagesManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;
import utils.DebugLogger;
import utils.TranslateColors;
import utils.Utils;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BanSpawnCommand implements CommandExecutor {

    private final BanManager banManager;
    private final TranslateColors translateColors;
    private final Utils utils;
    private final MessagesManager messagesManager;

    public BanSpawnCommand(@NotNull TSetSpawn plugin) {
        this.banManager = plugin.getBanManager();
        this.translateColors = plugin.getTranslateColors();
        this.utils = plugin.getUtils();
        this.messagesManager = plugin.getMessagesManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (!utils.hasPerm(sender, "tss.admin.ban")) return true;

        if (args.length < 1) {
            sendUsage(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "list" -> handleList(sender, args);
            case "add" -> handleAdd(sender, args);
            case "remove" -> handleRemove(sender, args);
            case "clear" -> handleClear(sender, args);
            default -> sendUsage(sender);
        }

        return true;
    }

    private void handleList(CommandSender sender, String @NotNull [] args) {
        if (args.length < 2) {
            sender.sendMessage(translateColors.translateColors(null,
                    messagesManager.getSpawnbanListUsage()));
            return;
        }

        int spawnId;
        try {
            spawnId = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(translateColors.translateColors(null, messagesManager.getSpawnbanInvalidId()));
            return;
        }

        Set<UUID> banned = banManager.getBannedPlayers(String.valueOf(spawnId));
        if (banned.isEmpty()) {
            sender.sendMessage(translateColors.translateColors(null,
                    messagesManager.getSpawnbanNoBans().replace("%spawn%", String.valueOf(spawnId))));
            return;
        }

        String bannedNames = banned.stream()
                .map(uuid -> {
                    OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
                    return offline.getName() != null ? offline.getName() : uuid.toString();
                })
                .collect(Collectors.joining(", "));

        sender.sendMessage(translateColors.translateColors(null,
                messagesManager.getSpawnbanList().replace("%spawn%", String.valueOf(spawnId)).replace("%players%", bannedNames)));
    }

    private void handleAdd(CommandSender sender, String @NotNull [] args) {
        if (args.length < 3) {
            sender.sendMessage(translateColors.translateColors(null,
                    messagesManager.getSpawnbanAddUsage()));
            return;
        }

        int spawnId;
        try {
            spawnId = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(translateColors.translateColors(null, messagesManager.getSpawnbanInvalidId()));
            return;
        }

        Player target = Bukkit.getPlayerExact(args[2]);
        if (target == null) {
            sender.sendMessage(translateColors.translateColors(null, messagesManager.getSpawnbanPlayerNotFound()));
            return;
        }

        boolean added = banManager.banPlayer(String.valueOf(spawnId), target.getUniqueId());
        if (!added) {
            sender.sendMessage(translateColors.translateColors(null, messagesManager.getSpawnbanAlreadyBanned()));
            return;
        }

        sender.sendMessage(translateColors.translateColors(null,
                messagesManager.getSpawnbanBanned()
                        .replace("%player%", target.getName())
                        .replace("%spawn%", String.valueOf(spawnId))));
        DebugLogger.log(sender.getName(), "has banned", target.getName(), "from spawn", spawnId);
    }

    private void handleRemove(CommandSender sender, String @NotNull [] args) {
        if (args.length < 3) {
            sender.sendMessage(translateColors.translateColors(null,
                    messagesManager.getSpawnbanRemoveUsage()));
            return;
        }

        int spawnId;
        try {
            spawnId = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(translateColors.translateColors(null, messagesManager.getSpawnbanInvalidId()));
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);

        boolean removed = banManager.unbanPlayer(String.valueOf(spawnId), target.getUniqueId());
        if (!removed) {
            sender.sendMessage(translateColors.translateColors(null, messagesManager.getSpawnbanNotBanned()));
            return;
        }

        sender.sendMessage(translateColors.translateColors(null,
                messagesManager.getSpawnbanUnbanned()
                        .replace("%player%", target.getName() != null ? target.getName() : "Unknown")
                        .replace("%spawn%", String.valueOf(spawnId))));
        DebugLogger.log(sender.getName(), "has unbanned", target.getName(), "from spawn", spawnId);
    }

    private void handleClear(CommandSender sender, String @NotNull [] args) {
        if (args.length < 2) {
            sender.sendMessage(translateColors.translateColors(null,
                    messagesManager.getSpawnbanClearUsage()));
            return;
        }

        int spawnId;
        try {
            spawnId = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(translateColors.translateColors(null, messagesManager.getSpawnbanInvalidId()));
            return;
        }

        int count = banManager.clearBans(String.valueOf(spawnId));
        sender.sendMessage(translateColors.translateColors(null,
                messagesManager.getSpawnbanCleared()
                        .replace("%count%", String.valueOf(count))
                        .replace("%spawn%", String.valueOf(spawnId))));
        DebugLogger.log(sender.getName(), "has cleared", count, "bans from spawn", spawnId);
    }

    private void sendUsage(@NotNull CommandSender sender) {
        sender.sendMessage(translateColors.translateColors(null, messagesManager.getSpawnbanUsageHeader()));
        sender.sendMessage(translateColors.translateColors(null, messagesManager.getSpawnbanUsageList()));
        sender.sendMessage(translateColors.translateColors(null, messagesManager.getSpawnbanUsageAdd()));
        sender.sendMessage(translateColors.translateColors(null, messagesManager.getSpawnbanUsageRemove()));
        sender.sendMessage(translateColors.translateColors(null, messagesManager.getSpawnbanUsageClear()));
    }
}
