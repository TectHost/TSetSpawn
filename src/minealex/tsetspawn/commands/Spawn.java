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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.md_5.bungee.api.chat.TextComponent;

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

        int waitTime = config.getInt("Config.Wait-time.time", 5);
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

        // Envía mensajes de conteo regresivo antes del teletransporte
        final int[] countdown = {waitTime};
        BukkitRunnable countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (countdown[0] > 0) {
                    ConfigurationSection waitTimeSection = config.getConfigurationSection("Config.Wait-time");
                    ConfigurationSection waitTimeMessages = waitTimeSection.getConfigurationSection("messages");

                    if (waitTimeMessages != null && waitTimeMessages.contains(String.valueOf(countdown[0]))) {
                        String countdownMessage = waitTimeMessages.getString(String.valueOf(countdown[0]));
                        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', countdownMessage));
                        sendTitleToPlayer(jugador, countdownMessage); // Envía el título al jugador
                    }

                    countdown[0]--;
                } else {
                    // Cuando el conteo regresivo termine, ejecutar el teletransporte
                    executeTeleport(jugador, l);
                    this.cancel(); // Cancelar el conteo regresivo
                }
            }
        };

        countdownTask.runTaskTimer(plugin, 0, 20); // Ejecutamos el BukkitRunnable cada segundo (20 ticks)

        return true;
    }

    private void executeTeleport(Player jugador, Location l) {
        // Elimina al jugador de la lista de espera antes de teletransportarlo
        cooldowns.remove(jugador.getName());

        boolean particlesEnabled = config.getBoolean("Config.Particles.enabled", false);

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
    }

    private void playTeleportSound(Player player) {
        // Reproducir el sonido si está habilitado en la configuración
        boolean enableSound = config.getBoolean("Config.Sound.enable-sound", false);
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

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player jugador = event.getPlayer();
        Location location = jugador.getLocation();

        // Verificar si el jugador está en la lista de espera para el teletransporte
        if (cooldowns.containsKey(jugador.getName())) {
            // Cancelar la cuenta regresiva
            Bukkit.getScheduler().cancelTasks(plugin);
            // Eliminar al jugador de la lista de espera
            cooldowns.remove(jugador.getName());

            // Obtener el mensaje personalizable del archivo de configuración
            String cancelMessage = config.getString("Config.Translate.move-cancel-movement", "&5TSetSpawn &e> &fTeleport canceled because you have moved.");
            
            // Enviar el mensaje al jugador
            jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', cancelMessage));
        }

        if (enableVoidTeleport && location.getY() < 0) {
            // Teletransportar al jugador al spawn
            Location spawnLocation = getSpawnLocationFromConfig();
            jugador.teleport(spawnLocation);

            // Restablecer la distancia de caída del jugador a cero para evitar daños
            jugador.setFallDistance(0);

            // Obtener el mensaje personalizable del archivo de configuración
            String voidTeleportMessage = config.getString("Config.Translate.void-teleport-message", "&5TSetSpawn &e> &fTeleported to the Spawn");

            // Enviar el mensaje al jugador
            jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', voidTeleportMessage));
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

    @SuppressWarnings("deprecation")
    private void sendTitleToPlayer(Player player, String message) {
        boolean titlesEnabled = config.getBoolean("Config.Wait-time.titles-enabled", true); // Lee la configuración de títulos

        if (titlesEnabled) {
            String titleMessage = config.getString("Config.Wait-time.title-message", "&aTeleporting!");
            String subtitleMessage = config.getString("Config.Wait-time.subtitle-message", "&eGet ready!");

            // Traduce los colores usando TextComponent
            TextComponent titleComponent = new TextComponent(ChatColor.translateAlternateColorCodes('&', titleMessage));
            TextComponent subtitleComponent = new TextComponent(ChatColor.translateAlternateColorCodes('&', subtitleMessage));

            // Envía el título y el subtítulo al jugador
            player.sendTitle(
                titleComponent.toLegacyText(),
                subtitleComponent.toLegacyText()
            );
        }
    }
}
