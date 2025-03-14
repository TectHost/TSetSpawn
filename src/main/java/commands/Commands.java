package commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;

public class Commands implements CommandExecutor {
    private final TSetSpawn plugin;

    public Commands(TSetSpawn plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("tss.admin")) {
            sendNoPermissionMessage(sender);
            return true;
        }

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
        if (sender.hasPermission(permission)) {
            commandAction.run();
        } else {
            sendNoPermissionMessage(sender);
        }
    }

    private void sendNoPermissionMessage(@NotNull CommandSender sender) {
        sender.sendMessage(plugin.getTranslateColors().translateColors(
                null, plugin.getMessagesManager().getNoPerm()
        ));
    }

    private void reloadConfigurations(@NotNull CommandSender sender) {
        plugin.getConfigManager().reloadConfig();
        plugin.getMessagesManager().reloadConfig();
        plugin.getSpawnsManager().reloadConfig();
        plugin.getSpawnMessagesManager().reloadMessages();
        sender.sendMessage(plugin.getTranslateColors().translateColors(
                null, plugin.getMessagesManager().getReload()
        ));
    }

    private void sendVersionInfo(@NotNull CommandSender sender) {
        sender.sendMessage(plugin.getTranslateColors().translateColors(
                null, plugin.getMessagesManager().getVersion().replace("%v%", plugin.getDescription().getVersion())
        ));
    }
}
