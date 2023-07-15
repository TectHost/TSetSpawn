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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Spawn implements CommandExecutor, Listener {
    private Plugin plugin;
    private Map<String, Long> cooldowns = new HashMap<>();
    private Set<String> firstTimeTeleport = new HashSet<>();
    private boolean enableVoidTeleport;
    private String voidTeleportMessage;
    private FileConfiguration config;

    public Spawn(Plugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        Bukkit.getPluginManager().registerEvents(this, plugin);

        boolean enableFirstTimeTeleport = config.getBoolean("Config.enable-first-time-teleport", true);
        enableVoidTeleport = config.getBoolean("Config.enable-void-teleport", true);
        voidTeleportMessage = config.getString("Config.Translate.void-teleport-message", "&5TSetSpawn &e> &fTeleported to the Spawn");

        if (!enableFirstTimeTeleport) {
            firstTimeTeleport.clear();
        }
    }

    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            String console = "Config.Translate.console";
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(console)));
            return false;
        }

        final Player jugador = (Player) sender;

        if (args.length > 0) {
            // Manejar los argumentos del comando aquí
        }

        if (!config.contains("Config.Spawn.x") || !config.contains("Config.Spawn.y") || !config.contains("Config.Spawn.z")) {
            String spawnNo = "Config.Translate.spawn-not-placed";
            jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(spawnNo)));
            return true;
        }

        if (firstTimeTeleport.contains(jugador.getName())) {
            jugador.teleport(getSpawnLocationFromConfig());
            firstTimeTeleport.remove(jugador.getName());
            String firstTimeTeleportMsg = "Config.Translate.first-time-teleport";
            jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(firstTimeTeleportMsg)).replaceAll("%player%", jugador.getName()));
            return true;
        }

        double x = Double.valueOf(config.getString("Config.Spawn.x"));
        double y = Double.valueOf(config.getString("Config.Spawn.y"));
        double z = Double.valueOf(config.getString("Config.Spawn.z"));
        float yaw = Float.valueOf(config.getString("Config.Spawn.yaw"));
        float pitch = Float.valueOf(config.getString("Config.Spawn.pitch"));
        World world = plugin.getServer().getWorld(config.getString("Config.Spawn.world"));
        final Location l = new Location(world, x, y, z, yaw, pitch);

        int waitTime = config.getInt("Config.wait-time", 5);
        int delayTicks = waitTime * 20;

        String playerName = jugador.getName();
        long currentTime = System.currentTimeMillis();

        if (cooldowns.containsKey(playerName)) {
            long lastUsage = cooldowns.get(playerName);
            long remainingTime = lastUsage + (waitTime * 1000) - currentTime;
            if (remainingTime > 0) {
                long secondsLeft = remainingTime / 1000;
                String cooldown = "Config.Translate.cooldown-message";
                jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(cooldown)).replace("%time%", String.valueOf(secondsLeft)));
                return true;
            }
        }

        cooldowns.put(playerName, currentTime);

        String countdownMessage = "Config.Translate.wait-time-message";
        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(countdownMessage)));

        new BukkitRunnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                boolean particlesEnabled = config.getBoolean("Config.Particles.enabled", true);

                if (particlesEnabled) {
                    double radius = config.getDouble("Config.Particles.radius", 1.5);
                    int particles = config.getInt("Config.Particles.amount", 50);
                    String particleType = config.getString("Config.Particles.particle-type", "MOBSPAWNER_FLAMES");

                    for (int i = 0; i < particles; i++) {
                        double angle = 2 * Math.PI * i / particles;
                        double offsetX = Math.cos(angle) * radius;
                        double offsetZ = Math.sin(angle) * radius;
                        Location particleLocation = l.clone().add(offsetX, 0.5, offsetZ);
                        jugador.playEffect(particleLocation, org.bukkit.Effect.valueOf(particleType), 0);
                    }
                }

                jugador.teleport(l);
                String onSpawn = "Config.Translate.spawn";
                jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(onSpawn)));
            }
        }.runTaskLater(plugin, delayTicks);

        return true;
    }

    private Location getSpawnLocationFromConfig() {
        double x = Double.parseDouble(config.getString("Config.Spawn.x"));
        double y = Double.parseDouble(config.getString("Config.Spawn.y"));
        double z = Double.parseDouble(config.getString("Config.Spawn.z"));
        float yaw = Float.parseFloat(config.getString("Config.Spawn.yaw"));
        float pitch = Float.parseFloat(config.getString("Config.Spawn.pitch"));
        World world = plugin.getServer().getWorld(config.getString("Config.Spawn.world"));
        return new Location(world, x, y, z, yaw, pitch);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player jugador = event.getPlayer();
        firstTimeTeleport.add(jugador.getName());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (enableVoidTeleport) {
            Player jugador = event.getPlayer();
            Location location = jugador.getLocation();
            if (location.getY() < 0) {
                jugador.teleport(getSpawnLocationFromConfig());
                jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', voidTeleportMessage));
            }
        }
    }
}
