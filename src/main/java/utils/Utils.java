package utils;

import managers.MessagesManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tsetspawn.TSetSpawn;

public class Utils {
    private final TSetSpawn plugin;
    private final MessagesManager messagesManager;
    private final TranslateColors translateColors;

    public Utils(@NotNull TSetSpawn plugin) {
        this.plugin = plugin;
        this.translateColors = plugin.getTranslateColors();
        this.messagesManager = plugin.getMessagesManager();
    }

    @Nullable
    public Player getPlayerIfExists(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(translateColors.translateColors(null, messagesManager.getOnlyPlayer()));
            return null;
        }
        return player;
    }

    public void sendNoPermissionMessage(@NotNull CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player) sender : null;
        sender.sendMessage(translateColors.translateColors(player, messagesManager.getNoPerm()));
    }

    public boolean hasPerm(@NotNull CommandSender sender, @NotNull String perm) {
        if (sender.hasPermission("tss.admin") || sender.hasPermission(perm)) {
            return true;
        } else {
            sendNoPermissionMessage(sender);
            return false;
        }
    }

    public static boolean isSafeLocation(Location location) {
        if (location == null || location.getWorld() == null) return false;

        Block feet = location.getBlock();
        Block head = feet.getRelative(0, 1, 0);
        Block ground = feet.getRelative(0, -1, 0);

        if (feet.getType().isSolid() || head.getType().isSolid()) return false;

        return ground.getType().isSolid() && !isDangerousBlock(ground.getType());
    }

    private static boolean isDangerousBlock(Material type) {
        return type == Material.LAVA
                || type == Material.FIRE
                || type == Material.CAMPFIRE
                || type == Material.SOUL_FIRE
                || type == Material.MAGMA_BLOCK
                || type == Material.CACTUS;
    }

    public void safeRun(String name, Runnable r) {
        try {
            r.run();
        } catch (Throwable t) {
            plugin.getLogger().warning("Error en " + name + ": " + t.getMessage());
            t.printStackTrace();
        }
    }
}
