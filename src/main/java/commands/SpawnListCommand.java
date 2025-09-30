package commands;

import managers.ConfigManager;
import managers.MessagesManager;
import managers.SpawnListManager;
import managers.SpawnsManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;
import utils.SpawnsGUI;
import utils.TranslateColors;
import utils.Utils;

import java.util.Map;

public class SpawnListCommand implements CommandExecutor, Listener {

    private final Utils utils;
    private final SpawnsManager spawnsManager;
    private final TranslateColors translateColors;
    private final MessagesManager messagesManager;
    private final SpawnsGUI spawnsGUI;
    private final SpawnListManager spawnListManager;

    @Contract(pure = true)
    public SpawnListCommand(@NotNull TSetSpawn plugin) {
        this.utils = plugin.getUtils();
        this.spawnsManager = plugin.getSpawnsManager();
        this.translateColors = plugin.getTranslateColors();
        this.messagesManager = plugin.getMessagesManager();
        this.spawnsGUI = plugin.getSpawnsGUI();
        this.spawnListManager = plugin.getSpawnListManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!utils.hasPerm(sender, "tss.spawnlist")) return true;

        if (spawnListManager.isGuiEnabled()) {
            Player player = utils.getPlayerIfExists(sender);
            if (player == null) return true;

            spawnsGUI.menu(player);
            return true;
        }

        Map<Integer, Location> spawns = spawnsManager.getAllSpawns();
        if (spawns.isEmpty()) {
            sender.sendMessage(translateColors.translateColors(null, messagesManager.getNoSpawns()));
            return true;
        }

        sender.sendMessage(translateColors.translateColors(null, messagesManager.getListHeader()));

        for (Map.Entry<Integer, Location> entry : spawns.entrySet()) {
            int id = entry.getKey();
            Location loc = entry.getValue();
            String world = loc.getWorld() != null ? loc.getWorld().getName() : "unknown";

            int x = (int) Math.round(loc.getX());
            int y = (int) Math.round(loc.getY());
            int z = (int) Math.round(loc.getZ());

            sender.sendMessage(translateColors.translateColors(null, messagesManager.getSpawnList()
                    .replace("%id%", String.valueOf(id))
                    .replace("%world%", world)
                    .replace("%x", String.valueOf(x))
                    .replace("%y", String.valueOf(y))
                    .replace("%z", String.valueOf(z))
            ));
        }

        return true;
    }
}