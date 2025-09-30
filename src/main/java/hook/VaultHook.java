package hook;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import utils.DebugLogger;

public class VaultHook {

    private static Economy econ = null;

    public static void setupEconomy(JavaPlugin plugin) {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            DebugLogger.log("[VaultHook] Vault plugin not found. Economy setup skipped.");
            return;
        }

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            DebugLogger.log("[VaultHook] No economy provider registered with Vault.");
            return;
        }

        econ = rsp.getProvider();
        DebugLogger.log("[VaultHook] Economy provider hooked: " + econ.getName());
    }

    public static boolean canAfford(Player player, double cost) {
        if (econ == null) {
            DebugLogger.log("[VaultHook] No economy provider found. Cannot charge.");
            return true;
        }

        if (!econ.isEnabled()) {
            DebugLogger.log("[VaultHook] Economy provider is not enabled.");
            return true;
        }

        if (!econ.has(player, cost)) {
            DebugLogger.log("[VaultHook] Player " + player.getName() + " does not have enough money. Required: " + cost);
            return false;
        }

        return true;
    }

    public static boolean charge(Player player, double amount) {
        if (econ == null) {
            DebugLogger.log("[VaultHook] No economy provider found. Cannot charge.");
            return true;
        }

        if (!econ.isEnabled()) {
            DebugLogger.log("[VaultHook] Economy provider is not enabled.");
            return true;
        }

        if (!econ.has(player, amount)) {
            DebugLogger.log("[VaultHook] Player " + player.getName() + " does not have enough money. Required: " + amount);
            return false;
        }

        econ.withdrawPlayer(player, amount);
        DebugLogger.log("[VaultHook] Charged " + amount + " from player " + player.getName());
        return true;
    }
}
