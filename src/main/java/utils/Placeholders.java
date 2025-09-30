package utils;

import managers.SpawnsManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tsetspawn.TSetSpawn;

public class Placeholders extends PlaceholderExpansion {

    private final TSetSpawn plugin;
    private final SpawnsManager spawnsManager;

    public Placeholders(@NotNull TSetSpawn plugin) {
        this.plugin = plugin;
        this.spawnsManager = plugin.getSpawnsManager();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "tsetspawn";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) { return ""; }

        if (identifier.startsWith("spawn_")) {
            String idStr = identifier.substring("spawn_".length());
            try {
                int id = Integer.parseInt(idStr);
                Location loc = spawnsManager.getSpawn(id);
                if (loc == null) return "No spawn set";
                return formatLocation(loc);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        switch (identifier) {
            case "spawn": {
                Location loc = spawnsManager.getSpawn(0);
                if (loc == null) return "No spawn set";
                return formatLocation(loc);
            }
            default:
                return null;
        }
    }

    private @NotNull String formatLocation(@NotNull Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }
}
