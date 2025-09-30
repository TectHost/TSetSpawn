package commands;

import managers.ConfigManager;
import managers.MessagesManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;
import utils.DebugLogger;
import utils.MenuGUI;
import utils.TranslateColors;
import utils.Utils;

import java.util.List;

public class Commands implements CommandExecutor {

    private final TSetSpawn plugin;
    private final ConfigManager configManager;
    private final TranslateColors translateColors;
    private final MessagesManager messagesManager;
    private final Utils utils;

    public Commands(@NotNull TSetSpawn plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.translateColors = plugin.getTranslateColors();
        this.messagesManager = plugin.getMessagesManager();
        this.utils = plugin.getUtils();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "reload" -> handleCommand(sender, "tss.reload", () -> reloadConfigurations(sender));
            case "version" -> handleCommand(sender, "tss.version", () -> sendVersionInfo(sender));
            case "menu" -> handleCommand(sender, "tss.menu", () -> menuCommand(sender));
            case "config" -> handleCommand(sender, "tss.config", () -> configCommand(sender, args));
            default -> { return false; }
        }

        return true;
    }

    private void handleCommand(@NotNull CommandSender sender, @NotNull String permission, @NotNull Runnable commandAction) {
        if (utils.hasPerm(sender, permission)) {
            commandAction.run();
        }
    }

    private void reloadConfigurations(@NotNull CommandSender sender) {
        DebugLogger.log("Executing reload command.");

        configManager.reloadConfig();
        messagesManager.reloadConfig();
        plugin.getSpawnsManager().reloadConfig();

        List<Runnable> reloadActions = List.of(
                () -> DebugLogger.init(plugin),
                () -> { if (configManager.isMessages()) { plugin.getSpawnMessagesManager().reloadMessages(); } },
                () -> { if (configManager.isJoin()) plugin.getJoinManager().reloadConfig(); },
                () -> { if (configManager.isVoidModule()) plugin.getVoidManager().reloadConfig(); },
                () -> { if (configManager.isTitlesModule()) plugin.getTitlesManager().reloadTitles(); },
                () -> { if (configManager.isActionBar()) plugin.getActionBarManager().reloadActionBar(); },
                () -> { if (configManager.isPermissions()) plugin.getPermissionsManager().reloadPermissions(); },
                () -> { if (configManager.isCooldown()) plugin.getCooldownManager().reloadCooldowns(); },
                () -> { if (configManager.isCountdown()) plugin.getCountdownManager().reloadCountdowns(); },
                () -> { if (configManager.isSounds()) plugin.getSoundsManager().reloadSounds(); },
                () -> { if (configManager.isParticles()) plugin.getParticlesManager().reloadParticles(); },
                () -> { if (configManager.isFireworks()) plugin.getFireworksManager().reloadFireworks(); },
                () -> { if (configManager.isWeb()) { plugin.getWebManager().reloadConfig(); plugin.getHttpServer().restart(); }},
                () -> { if (configManager.isGui()) plugin.getMenuManager().reloadConfig(); },
                () -> { if (configManager.isVault()) plugin.getVaultManager().reloadSpawns(); },
                () -> { if (configManager.isCommands()) plugin.getCommandsManager().reloadCommands(); },
                () -> { if (configManager.isAnimations()) plugin.getAnimationsManager().reloadConfig(); },
                () -> { if (configManager.isBans()) plugin.getBanManager().reloadBans(); }
        );

        reloadActions.forEach(Runnable::run);

        sender.sendMessage(translateColors.translateColors(null, messagesManager.getReload()));
    }

    private void configCommand(CommandSender sender, String @NotNull [] args) {
        if (args.length < 3) {
            sender.sendMessage(translateColors.translateColors(null, messagesManager.getConfigUsage()));
            return;
        }

        String key = args[1].toLowerCase();
        String rawValue = args[2];

        if (rawValue.equalsIgnoreCase("true") || rawValue.equalsIgnoreCase("false")) {
            boolean value = Boolean.parseBoolean(rawValue);

            String path = switch (key) {
                case "safe-location" -> "general." + key;
                case "messages", "join", "void", "titles", "actionbar", "permissions", "cooldown",
                     "countdown", "sounds", "particles", "fireworks", "respawn", "web", "gui",
                     "spawn-list", "spawn-gui", "commands", "animations", "bans" -> "modules." + key;
                case "debug" -> "debug";
                default -> null;
            };

            if (path != null) {
                configManager.setConfig(path, value);
                configManager.loadConfig();
                String formattedMessage = messagesManager.getConfigSet()
                        .replace("%key%", key)
                        .replace("%value%", String.valueOf(value));
                sender.sendMessage(translateColors.translateColors(null, formattedMessage));
            } else {
                sender.sendMessage(translateColors.translateColors(
                        null,
                        messagesManager.getConfigInvalidKey().replace("%key%", key)
                ));
            }

            return;
        }

        if (key.equalsIgnoreCase("lang-file") || key.equalsIgnoreCase("prefix")) {
            String path = "general." + key;
            configManager.setConfig(path, rawValue);
            configManager.loadConfig();

            String formattedMessage = messagesManager.getConfigSet()
                    .replace("%key%", key)
                    .replace("%value%", rawValue);
            sender.sendMessage(translateColors.translateColors(null, formattedMessage));
            return;
        }

        sender.sendMessage(translateColors.translateColors(null, messagesManager.getConfigInvalidValue()));
    }

    private void menuCommand(CommandSender sender) {
        Player player = utils.getPlayerIfExists(sender);
        if (player == null) return;

        MenuGUI gui = plugin.getMenuGUI();
        if (gui != null) {
            MenuGUI.open(player);
        }
    }

    private void sendHelpMessage(@NotNull CommandSender sender) {
        List<String> lines = messagesManager.getHelp();
        for (String line : lines) {
            sender.sendMessage(translateColors.translateColors(null, line));
        }
    }

    private void sendVersionInfo(@NotNull CommandSender sender) {
        String versionMessage = messagesManager.getVersion()
                .replace("%v%", plugin.getDescription().getVersion());

        sender.sendMessage(translateColors.translateColors(
                sender instanceof org.bukkit.entity.Player ? (org.bukkit.entity.Player) sender : null,
                versionMessage
        ));
    }
}
