package utils;

import managers.CountdownManager;
import managers.SpawnsManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;

import java.util.*;

public class CountdownHandler {

    private final TSetSpawn plugin;
    private final CountdownManager countdownManager;
    private final SpawnsManager spawnsManager;
    private final TranslateColors translateColors;

    private final Map<UUID, CountdownTask> activeCountdowns = new HashMap<>();

    public CountdownHandler(@NotNull TSetSpawn plugin) {
        this.plugin = plugin;
        this.countdownManager = plugin.getCountdownManager();
        this.spawnsManager = plugin.getSpawnsManager();
        this.translateColors = plugin.getTranslateColors();
    }

    public void startCountdown(@NotNull Player player, int spawnId, Runnable onFinish) {
        if (activeCountdowns.containsKey(player.getUniqueId())) {
            return;
        }

        CountdownManager.CountdownConfig config = countdownManager.getCountdown(spawnId);
        double durationDouble = config.getDuration();

        if (durationDouble <= 0) {
            teleport(player, spawnId);
            onFinish.run();
            return;
        }

        int duration = (int) Math.ceil(durationDouble);
        Location initialLocation = player.getLocation().clone();

        CountdownTask task = new CountdownTask(player, spawnId, duration, config, initialLocation, onFinish);
        task.runTaskTimer(plugin, 0L, 20L);
        activeCountdowns.put(player.getUniqueId(), task);
    }

    public void cancelCountdown(@NotNull Player player) {
        CountdownTask task = activeCountdowns.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    public void cancelAllCountdowns() {
        for (CountdownTask task : activeCountdowns.values()) {
            task.cancel();
        }
        activeCountdowns.clear();
    }

    private void teleport(Player player, int spawnId) {
        Location loc = spawnsManager.getSpawn(spawnId);
        if (loc != null) {
            player.teleport(loc);
        }
    }

    private class CountdownTask extends BukkitRunnable {
        private final Player player;
        private final int spawnId;
        private int secondsLeft;
        private final CountdownManager.CountdownConfig config;
        private final Location initialLocation;
        private final Runnable onFinish;

        public CountdownTask(Player player, int spawnId, int seconds,
                             CountdownManager.CountdownConfig config,
                             Location initialLocation,
                             Runnable onFinish) {
            this.player = player;
            this.spawnId = spawnId;
            this.secondsLeft = seconds;
            this.config = config;
            this.initialLocation = initialLocation;
            this.onFinish = onFinish;
        }

        @Override
        public void run() {
            if (!player.isOnline()) {
                cancelCountdown(player);
                return;
            }

            if (config.isCancelOnMove() && player.getLocation().distanceSquared(initialLocation) > 0.01) {
                player.sendMessage(translateColors.translateColors(player, plugin.getMessagesManager().getCountdown()));
                cancelCountdown(player);

                String soundS = config.getCancelSound();
                if (soundS == null || soundS.isEmpty()) return;

                try {
                    Sound sound = Sound.valueOf(soundS);
                    player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid cancel sound: " + soundS);
                }
                return;
            }

            config.getMessageForSecond(secondsLeft).ifPresent(message ->
                    player.sendMessage(translateColors.translateColors(player, message))
            );

            if (secondsLeft <= 0) {
                teleport(player, spawnId);
                cancelCountdown(player);
                onFinish.run();
                return;
            }

            secondsLeft--;
        }

        @Override
        public void cancel() {
            super.cancel();
            activeCountdowns.remove(player.getUniqueId());
        }
    }
}
