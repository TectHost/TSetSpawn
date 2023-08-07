package minealex.tsetspawn.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class Spawn implements CommandExecutor, Listener {
    private Plugin plugin;
    private Map<String, Long> cooldowns = new HashMap<>();
    private boolean enableVoidTeleport;
    private String voidTeleportMessage;
    private FileConfiguration config;

    public Spawn(Plugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        Bukkit.getPluginManager().registerEvents(this, plugin);

        enableVoidTeleport = config.getBoolean("Config.enable-void-teleport", true);
        voidTeleportMessage = config.getString("Config.Translate.void-teleport-message", "&5TSetSpawn &e> &fTeleported to the Spawn");
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

        double x = Double.valueOf(config.getString("Config.Spawn.x"));
        double y = Double.valueOf(config.getString("Config.Spawn.y"));
        double z = Double.valueOf(config.getString("Config.Spawn.z"));
        float yaw = Float.valueOf(config.getString("Config.Spawn.yaw"));
        float pitch = Float.valueOf(config.getString("Config.Spawn.pitch"));
        World world = plugin.getServer().getWorld(config.getString("Config.Spawn.world"));
        final Location l = new Location(world, x, y, z, yaw, pitch);

        int waitTime = config.getInt("Config.wait-time", 5);
        final int delayTicks = waitTime * 20;

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

        final Location originalLocation = jugador.getLocation();
        final long startTime = System.currentTimeMillis();
        final boolean[] teleported = {false}; // Usamos un array para almacenar el estado del teletransporte

        BukkitRunnable teleportTask = new BukkitRunnable() {
            @Override
            public void run() {
                // Check if the player is online
                boolean online = jugador.isOnline();

                // Check if the player has moved during the wait time
                boolean hasMoved = !jugador.getLocation().equals(originalLocation);

                int delayTime = config.getInt("Config.wait-time", 5); // Obtenemos el valor correcto de wait-time desde la configuración
                int delayTicksValue = delayTime * 20; // Calculamos el valor de delayTicksValue

                if (online && !teleported[0] && !hasMoved && (System.currentTimeMillis() - startTime) >= (delayTicksValue * 50)) {
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
                            jugador.getWorld().playEffect(particleLocation, Effect.valueOf(particleType), 0);
                        }
                    }

                    // Reproducir el sonido para el jugador justo antes de teletransportar
                    playTeleportSound(jugador);

                    jugador.teleport(l);
                    String onSpawn = "Config.Translate.spawn";
                    jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(onSpawn)));

                    teleported[0] = true; // Marcamos que el teletransporte ha sido realizado
                    this.cancel(); // Detenemos el BukkitRunnable para evitar que se repita
                } else if (!online) {
                    // El jugador ya no está en línea, se puede considerar cancelado
                	String disconnectMessage = config.getString("Config.Translate.disconnect-message", "&5TSetSpawn &e> &fTeleport canceled because you disconnected.");
                    jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', disconnectMessage));
                    this.cancel(); // Detenemos el BukkitRunnable si el jugador se desconecta
                } else if (hasMoved && config.getBoolean("Config.cancel-on-move", true)) {
                    // El jugador se ha movido y la cancelación por movimiento está habilitada
                	String moveCancelMessage = config.getString("Config.Translate.move-cancel-message", "&5TSetSpawn &e> &fTeleport canceled because you have moved.");
                    jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', moveCancelMessage));
                    this.cancel(); // Detenemos el BukkitRunnable si el jugador se mueve
                }
            }
        };

        teleportTask.runTaskTimer(plugin, 0, 1); // Ejecutamos el BukkitRunnable cada tick

        return true;
    }

    private void playTeleportSound(Player player) {
        // Reproducir el sonido si está habilitado en la configuración
        boolean enableSound = config.getBoolean("Config.Sound.enable-sound", true);
        if (enableSound) {
            String soundTypeString = config.getString("Config.Sound.sound-type", "LEVEL_UP");

            Sound soundType;
            try {
                soundType = Sound.valueOf(soundTypeString);
            } catch (IllegalArgumentException e) {
                // Use NOTE_PLING as a fallback sound for Bukkit 1.8.8
                soundType = Sound.valueOf("NOTE_PLING");
            }

            float volume = (float) config.getDouble("Config.Sound.volume", 1.0);
            float pitch = (float) config.getDouble("Config.Sound.pitch", 1.0);

            // Reproducir el sonido para el jugador
            player.getWorld().playSound(player.getLocation(), soundType, volume, pitch);
        }
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
