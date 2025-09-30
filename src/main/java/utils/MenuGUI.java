package utils;

import managers.ConfigManager;
import managers.MenuManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;

import java.util.ArrayList;
import java.util.List;

public class MenuGUI implements Listener {

    private static TSetSpawn plugin;
    private static ConfigManager configManager;
    private static MenuManager menuManager;
    private static TranslateColors translateColors;

    public MenuGUI(@NotNull TSetSpawn plugin) {
        MenuGUI.plugin = plugin;
        configManager = plugin.getConfigManager();
        menuManager = plugin.getMenuManager();
        translateColors = plugin.getTranslateColors();
    }

    public static void open(Player player) {
        String title = menuManager.getMenuTitle();
        Inventory gui = Bukkit.createInventory(null, 54, translateColors.translateColors(player, title));

        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.displayName(Component.empty());
            filler.setItemMeta(fillerMeta);
        }

        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta barrierMeta = barrier.getItemMeta();
        if (barrierMeta != null) {
            barrierMeta.displayName(translateColors.translateColors(player, menuManager.getCloseButtonName()));
            barrier.setItemMeta(barrierMeta);
        }

        for (int i = 0; i < 9; i++) {
            gui.setItem(i, filler);
            gui.setItem(45 + i, filler);
        }
        int[] borderSlots = {9, 17, 18, 26, 27, 35, 36, 44};
        for (int slot : borderSlots) {
            gui.setItem(slot, filler);
        }

        gui.setItem(49, barrier);

        ConfigurationSection section = menuManager.getModulesSection();
        if (section == null) {
            player.sendMessage(translateColors.translateColors(player, menuManager.getNoModulesMessage()));
            return;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection modSec = section.getConfigurationSection(key);
            if (modSec == null) continue;

            String matName = modSec.getString("material", "BARRIER");
            Material material = Material.matchMaterial(matName.toUpperCase());
            if (material == null) material = Material.BARRIER;

            String name = modSec.getString("name", "Module: " + key);
            List<String> loreLines = modSec.getStringList("description");
            int slot = modSec.getInt("slot", -1);
            if (slot < 0 || slot >= 54) continue;

            boolean enabled = isModuleEnabled(key);

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(translateColors.translateColors(player, name));

                List<Component> lore = new ArrayList<>();
                for (String line : loreLines) {
                    lore.add(translateColors.translateColors(player, line));
                }
                lore.add(Component.empty());
                lore.add(translateColors.translateColors(player, "<gray>Status: " + (enabled ? "<green><b>Enabled" : "<red><b>Disabled")));

                meta.lore(lore);
                item.setItemMeta(meta);
            }

            gui.setItem(slot, item);
        }

        player.openInventory(gui);
    }

    private static boolean isModuleEnabled(@NotNull String module) {
        return switch (module) {
            case "join" -> configManager.isJoin();
            case "messages" -> configManager.isMessages();
            case "void" -> configManager.isVoidModule();
            case "titles" -> configManager.isTitlesModule();
            case "actionbar" -> configManager.isActionBar();
            case "permissions" -> configManager.isPermissions();
            case "cooldown" -> configManager.isCooldown();
            case "countdown" -> configManager.isCountdown();
            case "sounds" -> configManager.isSounds();
            case "particles" -> configManager.isParticles();
            case "fireworks" -> configManager.isFireworks();
            case "respawn" -> configManager.isRespawn();
            case "web" -> configManager.isWeb();
            case "gui" -> configManager.isGui();
            default -> false;
        };
    }

    @EventHandler
    public void onClick(@NotNull InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().title().equals(translateColors.translateColors(player, menuManager.getMenuTitle()))) return;

        event.setCancelled(true);

        ItemStack current = event.getCurrentItem();
        if (current == null || current.getType() == Material.AIR) return;

        if (current.getType() == Material.BARRIER) {
            player.sendMessage(translateColors.translateColors(player, menuManager.getCloseMessage()));
            player.closeInventory();
            return;
        }

        ConfigurationSection section = menuManager.getModulesSection();
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection modSec = section.getConfigurationSection(key);
            if (modSec == null) continue;

            String matName = modSec.getString("material", "BARRIER");
            Material mat = Material.matchMaterial(matName.toUpperCase());
            if (mat == null || current.getType() != mat) continue;

            boolean currentState = isModuleEnabled(key);
            boolean newState = !currentState;

            player.performCommand("tss config " + key + " " + newState);

            int slot = modSec.getInt("slot", -1);
            if (slot >= 0 && slot < 54) {
                Bukkit.getScheduler().runTaskLater(plugin, () ->
                        event.getInventory().setItem(slot, buildUpdatedItem(key, modSec)), 2L);
            }

            return;
        }
    }

    private static @NotNull ItemStack buildUpdatedItem(String key, @NotNull ConfigurationSection modSec) {
        String matName = modSec.getString("material", "BARRIER");
        Material material = Material.matchMaterial(matName.toUpperCase());
        if (material == null) material = Material.BARRIER;

        String name = modSec.getString("name", "Module: " + key);
        List<String> loreLines = modSec.getStringList("description");
        boolean enabled = isModuleEnabled(key);

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(translateColors.translateColors(null, name));

            List<Component> lore = new ArrayList<>();
            for (String line : loreLines) {
                lore.add(translateColors.translateColors(null, line));
            }
            lore.add(Component.empty());
            lore.add(translateColors.translateColors(null, "<gray>Status: " + (enabled ? "<green><b>Enabled" : "<red><b>Disabled")));

            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
