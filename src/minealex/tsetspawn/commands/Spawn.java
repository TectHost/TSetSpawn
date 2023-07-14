package minealex.tsetspawn.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import minealex.tsetspawn.TSetSpawn;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class Spawn implements CommandExecutor {
    private TSetSpawn plugin;
    private HashMap<String, Long> cooldowns = new HashMap<>();
    private FileConfiguration config;

    public Spawn(TSetSpawn plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            String console = "Config.Translate.console";
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(console)));
            return false;
        } else {
            Player jugador = (Player) sender;
            if (args.length > 0) {
                // Manejar los argumentos del comando aquí
            }
            if (config.contains("Config.Spawn.x")) {
                double x = Double.valueOf(config.getString("Config.Spawn.x"));
                double y = Double.valueOf(config.getString("Config.Spawn.y"));
                double z = Double.valueOf(config.getString("Config.Spawn.z"));
                float yaw = Float.valueOf(config.getString("Config.Spawn.yaw"));
                float pitch = Float.valueOf(config.getString("Config.Spawn.pitch"));
                World world = plugin.getServer().getWorld(config.getString("Config.Spawn.world"));
                final Location l = new Location(world, x, y, z, yaw, pitch);

                int waitTime = config.getInt("Config.wait-time", 5);
                int delayTicks = waitTime * 20;

                if (cooldowns.containsKey(jugador.getName())) {
                    long lastUsage = cooldowns.get(jugador.getName());
                    long currentTime = System.currentTimeMillis();
                    long remainingTime = lastUsage + (waitTime * 1000) - currentTime;
                    if (remainingTime > 0) {
                        long secondsLeft = remainingTime / 1000;
                        String cooldown = "Config.Translate.cooldown-message";
                        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(cooldown)).replace("%time%", String.valueOf(secondsLeft)));
                        return true;
                    }
                }

                cooldowns.put(jugador.getName(), System.currentTimeMillis());

                String countdownMessage = "Config.Translate.wait-time-message";
                jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(countdownMessage)));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Player jugador = (Player) sender;
                        jugador.teleport(l);
                        String onSpawn = "Config.Translate.spawn";
                        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(onSpawn)));
                    }
                }.runTaskLater(plugin, delayTicks);

                return true;
            } else {
                String spawnNo = "Config.Translate.spawn-not-placed";
                jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(spawnNo)));
                return true;
            }
        }
    }
}
