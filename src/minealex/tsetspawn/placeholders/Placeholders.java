package minealex.tsetspawn.placeholders;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import minealex.tsetspawn.TSetSpawn;

public class Placeholders extends PlaceholderExpansion {

    private TSetSpawn plugin;

    public Placeholders(TSetSpawn plugin) {
        this.plugin = plugin;
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
    public String getAuthor() {
        return "Mine_Alex";
    }

    @Override
    public String getIdentifier() {
        return "tsetspawn";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        if (identifier.equals("spawn")) {
            double spawnX = Double.parseDouble(plugin.getConfig().getString("Config.Spawn.x"));
            double spawnY = Double.parseDouble(plugin.getConfig().getString("Config.Spawn.y"));
            double spawnZ = Double.parseDouble(plugin.getConfig().getString("Config.Spawn.z"));

            // Format the spawn coordinates
            return String.format("%.1f, %.1f, %.1f", spawnX, spawnY, spawnZ);
        }
        
        if (identifier.equals("ftspawn")) {
            double ftspawnX = Double.parseDouble(plugin.getConfig().getString("Config.FTSpawn.x"));
            double ftspawnY = Double.parseDouble(plugin.getConfig().getString("Config.FTSpawn.y"));
            double ftspawnZ = Double.parseDouble(plugin.getConfig().getString("Config.FTSpawn.z"));

            // Format the spawn coordinates
            return String.format("%.1f, %.1f, %.1f", ftspawnX, ftspawnY, ftspawnZ);
        }
        
        if (identifier.equals("spawn_formatted")) {
            int spawnX = (int) Double.parseDouble(plugin.getConfig().getString("Config.Spawn.x"));
            int spawnY = (int) Double.parseDouble(plugin.getConfig().getString("Config.Spawn.y"));
            int spawnZ = (int) Double.parseDouble(plugin.getConfig().getString("Config.Spawn.z"));

            // Format the spawn coordinates without decimals or commas
            return String.format("%d %d %d", spawnX, spawnY, spawnZ);
        }
        
        if (identifier.equals("ftspawn_formatted")) {
            int ftspawnX = (int) Double.parseDouble(plugin.getConfig().getString("Config.FTSpawn.x"));
            int ftspawnY = (int) Double.parseDouble(plugin.getConfig().getString("Config.FTSpawn.y"));
            int ftspawnZ = (int) Double.parseDouble(plugin.getConfig().getString("Config.FTSpawn.z"));

            // Format the spawn coordinates without decimals or commas
            return String.format("%d %d %d", ftspawnX, ftspawnY, ftspawnZ);
        }

        return null;
    }
}
