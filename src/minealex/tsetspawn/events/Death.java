package minealex.tsetspawn.events;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import minealex.tsetspawn.TSetSpawn;

public class Death implements Listener {

    private TSetSpawn plugin;

    public Death(TSetSpawn plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        FileConfiguration config = plugin.getConfig();

        // Drop items on death
        if (config.getBoolean("Config.drop-items-on-death", true)) {
            Location deathLocation = event.getEntity().getLocation();
            World world = deathLocation.getWorld();

            for (ItemStack item : event.getDrops()) {
                world.dropItemNaturally(deathLocation, item);
            }

            event.getDrops().clear();
        }

        // Teleport on death
        if (config.getBoolean("Config.death-teleport", true)) {
            Location spawnLocation = getSpawnLocationFromConfig();

            event.getEntity().spigot().respawn(); // Respawning the player
            event.getEntity().teleport(spawnLocation);
        }
    }

    private Location getSpawnLocationFromConfig() {
        FileConfiguration config = plugin.getConfig();
        World world = plugin.getServer().getWorld(config.getString("Config.Spawn.world"));
        double x = config.getDouble("Config.Spawn.x");
        double y = config.getDouble("Config.Spawn.y");
        double z = config.getDouble("Config.Spawn.z");
        float yaw = (float) config.getDouble("Config.Spawn.yaw");
        float pitch = (float) config.getDouble("Config.Spawn.pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }
}
