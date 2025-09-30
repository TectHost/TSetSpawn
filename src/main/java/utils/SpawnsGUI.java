package utils;

import managers.SpawnListManager;
import managers.SpawnsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpawnsGUI implements Listener {

    private final TranslateColors translateColors;
    private final SpawnsManager spawnsManager;
    private final SpawnListManager spawnListManager;

    @Contract(pure = true)
    public SpawnsGUI(@NotNull TSetSpawn plugin) {
        this.translateColors = plugin.getTranslateColors();
        this.spawnsManager = plugin.getSpawnsManager();
        this.spawnListManager = plugin.getSpawnListManager();
    }

    public void menu(Player player) {
        Map<Integer, Location> spawns = spawnsManager.getAllSpawns();
        int itemsPerPage = 28;
        int page = 0;

        openSpawnMenu(player, spawns, page, itemsPerPage);
    }

    private void openSpawnMenu(Player player, @NotNull Map<Integer, Location> spawns, int page, int itemsPerPage) {
        int totalPages = (int) Math.ceil(spawns.size() / (double) itemsPerPage);

        Component title = translateColors.translateColors(player, spawnListManager.getTitleText().replace("%page%", String.valueOf(page + 1)));
        Inventory inv = Bukkit.createInventory(null, 54, title);

        ItemStack border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        if (borderMeta != null) {
            borderMeta.displayName(Component.text(" "));
            border.setItemMeta(borderMeta);
        }

        int[] borderSlots = {
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                9, 17,
                18, 26,
                27, 35,
                36, 44,
                45, 46, 47, 48, 50, 51, 52, 53
        };
        for (int slot : borderSlots) {
            inv.setItem(slot, border);
        }

        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        if (closeMeta != null) {
            closeMeta.displayName(translateColors.translateColors(player, spawnListManager.getCloseText()));
            close.setItemMeta(closeMeta);
        }
        inv.setItem(49, close);

        List<Map.Entry<Integer, Location>> entries = new ArrayList<>(spawns.entrySet());
        int start = page * itemsPerPage;
        int end = Math.min(start + itemsPerPage, spawns.size());

        for (int i = start; i < end; i++) {
            Map.Entry<Integer, Location> entry = entries.get(i);
            int id = entry.getKey();
            Location loc = entry.getValue();
            String world = loc.getWorld() != null ? loc.getWorld().getName() : "unknown";

            ItemStack bed = new ItemStack(Material.RED_BED);
            ItemMeta meta = bed.getItemMeta();
            if (meta != null) {
                meta.displayName(translateColors.translateColors(player, spawnListManager.getSpawnName().replace("%id%", String.valueOf(id))));
                meta.lore(List.of(
                        translateColors.translateColors(player, spawnListManager.getWorldText().replace("%world%", world)),
                        translateColors.translateColors(player, spawnListManager.getXText().replace("%x%", String.valueOf(loc.getBlockX()))),
                        translateColors.translateColors(player, spawnListManager.getYText().replace("%y%", String.valueOf(loc.getBlockY()))),
                        translateColors.translateColors(player, spawnListManager.getZText().replace("%z%", String.valueOf(loc.getBlockZ())))
                ));
                bed.setItemMeta(meta);
            }

            inv.addItem(bed);
        }

        if (page > 0) {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prev.getItemMeta();
            if (prevMeta != null) {
                prevMeta.displayName(translateColors.translateColors(player, spawnListManager.getPreviousText()));
                prev.setItemMeta(prevMeta);
            }
            inv.setItem(48, prev);
        }

        if (page < totalPages - 1) {
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = next.getItemMeta();
            if (nextMeta != null) {
                nextMeta.displayName(translateColors.translateColors(player, spawnListManager.getNextText()));
                next.setItemMeta(nextMeta);
            }
            inv.setItem(50, next);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String savedSpawn = spawnListManager.getTitleText();

        String titleText = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        String titleTextClean = titleText.replaceAll("\\d+", "");

        String targetText = PlainTextComponentSerializer.plainText().serialize(
                translateColors.translateColors(player, savedSpawn.replace("%page%", ""))
        );

        if (!titleTextClean.contains(targetText)) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        ItemMeta meta = clicked.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        String name = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(meta.displayName()));

        int page = 0;
        Matcher matcher = Pattern.compile("(\\d+)").matcher(titleText);
        if (matcher.find()) {
            page = Integer.parseInt(matcher.group(1)) - 1;
        }

        switch (clicked.getType()) {
            case BARRIER -> player.closeInventory();
            case ARROW -> {
                String savedName = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(translateColors.translateColors(player, spawnListManager.getPreviousText())));
                if (name.equalsIgnoreCase(savedName)) {
                    openSpawnMenu(player, spawnsManager.getAllSpawns(), page - 1, 28);
                } else {
                    Bukkit.getConsoleSender().sendMessage(String.valueOf(page));
                    openSpawnMenu(player, spawnsManager.getAllSpawns(), page + 1, 28);
                }
            }
            case RED_BED -> {
                Component title = clicked.getItemMeta().displayName();
                assert title != null;
                String plainTitle = PlainTextComponentSerializer.plainText().serialize(title);

                char lastChar = plainTitle.charAt(plainTitle.length() - 1);

                if (Character.isDigit(lastChar)) {
                    player.performCommand("spawn " + lastChar);
                } else {
                    player.sendMessage(translateColors.translateColors(player, spawnListManager.getErrorId()));
                }

                player.closeInventory();
            }
        }
    }
}
