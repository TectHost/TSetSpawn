package commands;

import managers.ConfigManager;
import managers.MessagesManager;
import managers.SpawnsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;
import utils.TranslateColors;

import java.time.Duration;

public class Spawn implements CommandExecutor {
    private final TSetSpawn plugin;
    private final SpawnsManager spawnsManager;
    private final TranslateColors translateColors;
    private final MessagesManager messagesManager;
    private final ConfigManager configManager;

    @Contract(pure = true)
    public Spawn(@NotNull TSetSpawn plugin) {
        this.plugin = plugin;
        this.spawnsManager = plugin.getSpawnsManager();
        this.translateColors = plugin.getTranslateColors();
        this.messagesManager = plugin.getMessagesManager();
        this.configManager = plugin.getConfigManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(translateColors.translateColors(null, messagesManager.getOnlyPlayer()));
            return true;
        }

        int spawnId = 0;

        if (args.length >= 2) {
            try {
                spawnId = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(translateColors.translateColors(player, messagesManager.getInvalidSpawn()));
                return true;
            }
        }

        Location spawnLocation = spawnsManager.getSpawn(spawnId);
        if (spawnLocation == null) {
            player.sendMessage(translateColors.translateColors(player, messagesManager.getNotExist().replace("%spawn%", String.valueOf(spawnId))));
            return true;
        }

        if (configManager.isPermissions()) {
            String permission = plugin.getPermissionsManager().getSpawnPermission(spawnId);
            if (!player.hasPermission(permission)) {
                player.sendMessage(translateColors.translateColors(player, messagesManager.getNoPerm()));
                return true;
            }
        }

        player.teleport(spawnLocation);
        if (configManager.isMessages()) sendMessage(player, spawnId);
        if (configManager.isTitlesModule()) sendTitle(player, spawnId);
        if (configManager.isActionBar()) sendActionBar(player, spawnId);
        return true;
    }

    public void sendActionBar(Player player, int spawnId) {
        String actionBarData = plugin.getActionBarManager().getSpawnActionBar(spawnId);

        if (actionBarData.isEmpty()) {
            return;
        }

        Component actionBar = translateColors.translateColors(player, actionBarData);

        player.sendActionBar(actionBar);
    }

    public void sendTitle(Player player, int spawnId) {
        String titleData = plugin.getTitlesManager().getSpawnTitle(spawnId);

        if (!titleData.contains(";")) {
            return;
        }

        String[] parts = titleData.split(";", 2);
        String titleText = parts.length > 0 ? parts[0] : "";
        String subtitleText = parts.length > 1 ? parts[1] : "";

        Component titleComponent = translateColors.translateColors(player, titleText);
        Component subtitleComponent = translateColors.translateColors(player, subtitleText);

        Title title = Title.title(
                titleComponent,
                subtitleComponent,
                Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1))
        );

        player.showTitle(title);
    }

    public void sendMessage(Player player, int spawnId) {
        String message = plugin.getSpawnMessagesManager().getSpawnMessage(spawnId);
        if (!message.isEmpty()) {
            player.sendMessage(translateColors.translateColors(player, message));
        }
    }
}
