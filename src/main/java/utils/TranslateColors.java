package utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;

public class TranslateColors {

    private final TSetSpawn plugin;

    public TranslateColors(TSetSpawn plugin) {
        this.plugin = plugin;
    }

    public Component translateColors(Player player, String message) {
        if (player != null) {
            message = PlaceholderAPI.setPlaceholders(player, message);
            if (message.contains("%player%")) { message = message.replace("%player%", player.getName()); }
        }

        message = applyCustomPlaceholders(message, player);

        return MiniMessage.miniMessage()
                .deserialize(message)
                .decoration(TextDecoration.ITALIC, false);
    }

    private @NotNull String applyCustomPlaceholders(@NotNull String message, Player player) {
        if (message.contains("%player%") && player != null) {
            message = message.replace("%player%", player.getName());
        }

        if (message.contains("%prefix%")) {
            message = message.replace("%prefix%", plugin.getConfigManager().getPrefix());
        }

        if (message.contains("%unique%")) {
            message = message.replace("%unique%", String.valueOf(plugin.getDataManager().getRegisteredPlayersCount()));
        }

        return message;
    }
}
