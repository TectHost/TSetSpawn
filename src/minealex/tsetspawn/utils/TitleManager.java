package minealex.tsetspawn.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TitleManager {

    @SuppressWarnings("deprecation")
	public static void sendTitle(Player player, String title, String subtitle) {
        player.sendTitle(ChatColor.translateAlternateColorCodes('&', title), ChatColor.translateAlternateColorCodes('&', subtitle));
    }
}
