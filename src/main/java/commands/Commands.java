package commands;

import managers.ConfigManager;
import managers.MessagesManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;
import utils.TranslateColors;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Commands implements CommandExecutor {
    private final TSetSpawn plugin;
    private final ConfigManager configManager;
    private final TranslateColors translateColors;
    private final MessagesManager messagesManager;

    @Contract(pure = true)
    public Commands(@NotNull TSetSpawn plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.translateColors = plugin.getTranslateColors();
        this.messagesManager = plugin.getMessagesManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length > 0) {
            return switch (args[0].toLowerCase()) {
                case "reload" -> {
                    handleCommand(sender, "tss.reload", () -> reloadConfigurations(sender));
                    yield true;
                }
                case "version" -> {
                    handleCommand(sender, "tss.version", () -> sendVersionInfo(sender));
                    yield true;
                }
                default -> false;
            };
        }

        return false;
    }

    private void handleCommand(@NotNull CommandSender sender, String permission, Runnable commandAction) {
        if (sender.hasPermission(permission) || sender.hasPermission("tss.admin")) {
            commandAction.run();
        } else {
            sendNoPermissionMessage(sender);
        }
    }

    private void sendNoPermissionMessage(@NotNull CommandSender sender) {
        sender.sendMessage(translateColors.translateColors(
                null, messagesManager.getNoPerm()
        ));
    }

    private void reloadConfigurations(@NotNull CommandSender sender) {
        Map<Supplier<Boolean>, Runnable> reloads = new LinkedHashMap<>();

        configManager.reloadConfig();
        messagesManager.reloadConfig();

        reloads.put(configManager::isMessages, () -> {
            plugin.getSpawnsManager().reloadConfig();
            plugin.getSpawnMessagesManager().reloadMessages();
        });
        reloads.put(configManager::isJoin, () -> plugin.getJoinManager().reloadConfig());
        reloads.put(configManager::isVoidModule, () -> plugin.getVoidManager().reloadConfig());
        reloads.put(configManager::isTitlesModule, () -> plugin.getTitlesManager().reloadTitles());
        reloads.put(configManager::isActionBar, () -> plugin.getActionBarManager().reloadActionBar());
        reloads.put(configManager::isPermissions, () -> plugin.getPermissionsManager().reloadPermissions());

        reloads.forEach((condition, action) -> {
            if (condition.get()) {
                action.run();
            }
        });

        sender.sendMessage(translateColors.translateColors(
                null, messagesManager.getReload()
        ));
    }

    private void sendVersionInfo(@NotNull CommandSender sender) {
        sender.sendMessage(translateColors.translateColors(
                null, messagesManager.getVersion().replace("%v%", plugin.getDescription().getVersion())
        ));
    }
}
