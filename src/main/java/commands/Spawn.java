package commands;

import hook.VaultHook;
import managers.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tsetspawn.TSetSpawn;
import utils.*;

import java.time.Duration;
import java.util.Arrays;

public class Spawn implements CommandExecutor {
    private static final Title.Times DEFAULT_TITLE_TIMES = Title.Times.times(
            Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1));

    private final TSetSpawn plugin;
    private final SpawnsManager spawnsManager;
    private final TranslateColors translateColors;
    private final MessagesManager messagesManager;
    private final ConfigManager configManager;
    private final Utils utils;
    private final CooldownHandler cooldown;
    private final CountdownHandler countdownHandler;
    private final SoundsManager soundsManager;
    private final ParticlesManager particlesManager;
    private final ActionBarManager actionBarManager;
    private final TitlesManager titlesManager;
    private final SpawnMessagesManager spawnMessagesManager;
    private final SpawnsGUI spawnsGUI;
    private final VaultManager vaultManager;
    private final CommandsManager commandsManager;
    private final AnimationsManager animationsManager;
    private final BanManager banManager;

    @Contract(pure = true)
    public Spawn(@NotNull TSetSpawn plugin) {
        this.plugin = plugin;
        this.spawnsManager = plugin.getSpawnsManager();
        this.translateColors = plugin.getTranslateColors();
        this.messagesManager = plugin.getMessagesManager();
        this.configManager = plugin.getConfigManager();
        this.utils = plugin.getUtils();
        this.cooldown = plugin.getCooldownHandler();
        this.countdownHandler = plugin.getCountdownHandler();
        this.soundsManager = plugin.getSoundsManager();
        this.particlesManager = plugin.getParticlesManager();
        this.actionBarManager = plugin.getActionBarManager();
        this.titlesManager = plugin.getTitlesManager();
        this.spawnMessagesManager = plugin.getSpawnMessagesManager();
        this.spawnsGUI = plugin.getSpawnsGUI();
        this.vaultManager = plugin.getVaultManager();
        this.commandsManager = plugin.getCommandsManager();
        this.animationsManager = plugin.getAnimationsManager();
        this.banManager = plugin.getBanManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        DebugLogger.log("Executing /spawn command. Args:", Arrays.toString(args));

        Player player = utils.getPlayerIfExists(sender);
        if (player == null) return true;

        int spawnId = parseSpawnId(args, player);
        if (spawnId == -1) return true;
        DebugLogger.log(player.getName(), "requested spawn ID:", spawnId);

        // SpawnGUI module
        if (args.length == 0 && configManager.isSpawnGui()) {
            DebugLogger.log("No args provided and GUI is enabled. Executing GUI.");

            spawnsGUI.menu(player);
            return true;
        }

        Location spawnLocation = spawnsManager.getSpawn(spawnId);
        if (spawnLocation == null) {
            DebugLogger.warn("Spawn ID", spawnId, "does not exist.");
            player.sendMessage(translateColors.translateColors(player, messagesManager.getNotExist().replace("%spawn%", String.valueOf(spawnId))));
            return true;
        }
        DebugLogger.log("Spawn location found:", spawnLocation);

        TargetInfo targetInfo = resolveTarget(args, player);
        if (targetInfo == null) return true;
        DebugLogger.log("Teleporting target:", targetInfo.target().getName(), "SendAll:", targetInfo.sendAll(), "SendOtherPlayer:", targetInfo.sendOtherPlayer());

        // Ban check
        if (configManager.isBans()) {
            String spawnKey = String.valueOf(spawnId);
            if (banManager.isBanned(spawnKey, targetInfo.target().getUniqueId())) {
                DebugLogger.warn(targetInfo.target().getName(), "is banned from spawn", spawnId);
                targetInfo.target().sendMessage(translateColors.translateColors(
                        targetInfo.target(),
                        messagesManager.getBanned().replace("%spawn%", spawnKey)
                ));
                return true;
            }
        }

        // Perm module
        if (configManager.isPermissions() && !utils.hasPerm(player, plugin.getPermissionsManager().getSpawnPermission(spawnId))) {
            DebugLogger.warn(player.getName(), "does not have permission for spawn ID", spawnId);
            return true;
        }

        // Cooldown module
        if (configManager.isCooldown()) {
            if (!cooldown.canUse(player, spawnId)) {
                double remaining = cooldown.getRemaining(player, spawnId);
                DebugLogger.log("Cooldown active for", player.getName(), "->", remaining, "seconds remaining");
                player.sendMessage(translateColors.translateColors(player, messagesManager.getCooldown().replace("%seconds%", String.format("%.2f", remaining))));
                return true;
            }
            cooldown.updateLastUse(player, spawnId);
            DebugLogger.log("Cooldown updated for", player.getName(), "on spawn ID", spawnId);
        }

        // Check money
        double cost;
        if (configManager.isVault()) {
            cost = vaultManager.getSpawnPrice(spawnId);
            if (cost > 0 && !VaultHook.canAfford(targetInfo.target(), cost)) {
                targetInfo.target().sendMessage(translateColors.translateColors(targetInfo.target(), messagesManager.getNoMoney()));
                return true;
            }
        } else {cost = 0;}

        // Countdown + teleport
        if (configManager.isCountdown()) {
            DebugLogger.log("Starting countdown for", targetInfo.target().getName(), "on spawn ID", spawnId);
            countdownHandler.startCountdown(targetInfo.target(), spawnId, () -> teleport(targetInfo, spawnLocation, player, spawnId, cost));
        } else {
            DebugLogger.log("No countdown. Teleporting immediately.");
            teleport(targetInfo, spawnLocation, player, spawnId, cost);
        }

        return true;
    }

    private void vaultHook(Player player, double cost) {
        if (!configManager.isVault()) return;

        if (cost <= 0) return;

        if (!VaultHook.charge(player, cost)) {
            player.sendMessage(translateColors.translateColors(player, messagesManager.getNoMoney()));
            return;
        }

        player.sendMessage(translateColors.translateColors(player, messagesManager.getPaidMsg()
                .replace("%amount%", String.valueOf(cost))));
    }

    private int parseSpawnId(String @NotNull [] args, Player player) {
        if (args.length >= 1) {
            try {
                return Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(translateColors.translateColors(player, messagesManager.getInvalidSpawn()));
                return -1;
            }
        }
        return 0;
    }

    private @Nullable TargetInfo resolveTarget(String @NotNull [] args, Player player) {
        if (args.length < 2) {
            return new TargetInfo(player, false, false);
        }

        if (args[1].equalsIgnoreCase("all")) {
            return new TargetInfo(player, false, true);
        }

        Player target = plugin.getServer().getPlayerExact(args[1]);
        if (target == null) {
            player.sendMessage(translateColors.translateColors(player, messagesManager.getPlayerNotFound()));
            return null;
        }

        return new TargetInfo(target, true, false);
    }

    private void teleport(@NotNull TargetInfo info, Location location, Player player, int spawnId, double cost) {
        DebugLogger.log("Teleporting", info.sendAll ? "all players" : info.target().getName(), "to spawn", spawnId);

        boolean messages = configManager.isMessages();
        boolean titles = configManager.isTitlesModule();
        boolean actionBar = configManager.isActionBar();
        boolean sounds = configManager.isSounds();
        boolean particles = configManager.isParticles();
        boolean fireworks = configManager.isFireworks();
        boolean commands = configManager.isCommands();
        boolean animations = configManager.isAnimations();

        if (info.sendAll) {
            for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                if (animations) {
                    animationsManager.playAnimation(onlinePlayer, spawnId, () -> {
                        teleportAfterAnimation(onlinePlayer, location, player, spawnId, cost,
                                messages, titles, actionBar, sounds, particles, fireworks, commands);
                    });
                } else {
                    teleportAfterAnimation(onlinePlayer, location, player, spawnId, cost,
                            messages, titles, actionBar, sounds, particles, fireworks, commands);
                }
            }
            return;
        }

        if (animations) {
            animationsManager.playAnimation(info.target(), spawnId, () -> {
                teleportAfterAnimation(info.target(), location, player, info.sendOtherPlayer, spawnId, cost,
                        messages, titles, actionBar, sounds, particles, fireworks, commands);
            });
        } else {
            teleportAfterAnimation(info.target(), location, player, info.sendOtherPlayer, spawnId, cost,
                    messages, titles, actionBar, sounds, particles, fireworks, commands);
        }
    }

    private void teleportAfterAnimation(@NotNull Player target, Location location, Player player, boolean sendOtherPlayer,
                                        int spawnId, double cost, boolean messages, boolean titles, boolean actionBar,
                                        boolean sounds, boolean particles, boolean fireworks, boolean commands) {

        target.teleport(location);
        vaultHook(target, cost);
        DebugLogger.log("Teleported", target.getName(), "to spawn", spawnId);

        if (messages) sendMessage(target, spawnId);
        if (titles) sendTitle(target, spawnId);
        if (actionBar) sendActionBar(target, spawnId);
        if (sounds) sendSound(target, spawnId);
        if (particles) sendParticle(target, spawnId);
        if (fireworks) sendFirework(target, spawnId);
        if (commands) commandsManager.executeCommands(spawnId, player);

        if (sendOtherPlayer) {
            player.sendMessage(translateColors.translateColors(player,
                    messagesManager.getTeleportSuccessOther().replace("%target%", target.getName())));
            target.sendMessage(translateColors.translateColors(target,
                    messagesManager.getYouWereTeleported().replace("%player%", player.getName())));
        }
    }

    private void teleportAfterAnimation(@NotNull Player target, Location location, Player executor, int spawnId, double cost,
                                        boolean messages, boolean titles, boolean actionBar, boolean sounds,
                                        boolean particles, boolean fireworks, boolean commands) {

        target.teleport(location);
        vaultHook(target, cost);
        DebugLogger.log("Teleported", target.getName(), "to spawn", spawnId);

        if (messages) sendMessage(target, spawnId);
        if (titles) sendTitle(target, spawnId);
        if (actionBar) sendActionBar(target, spawnId);
        if (sounds) sendSound(target, spawnId);
        if (particles) sendParticle(target, spawnId);
        if (fireworks) sendFirework(target, spawnId);
        if (commands) commandsManager.executeCommands(spawnId, executor);
    }

    public void sendFirework(Player player, int spawnId) {
        FireworkEffect effect = plugin.getFireworksManager().getEffect(spawnId);
        FireworksHandler.launchFirework(player, effect);
    }

    public void sendParticle(Player player, int spawnId) {
        Particle particle = particlesManager.getSpawnParticle(spawnId);
        if (particle != null) {
            player.spawnParticle(particle, player.getLocation(), 20);
        }
    }

    public void sendSound(Player player, int spawnId) {
        Sound sound = soundsManager.getSpawnSound(spawnId);
        if (sound != null) {
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }

    public void sendActionBar(Player player, int spawnId) {
        String actionBarData = actionBarManager.getSpawnActionBar(spawnId);
        if (!actionBarData.isEmpty()) {
            Component actionBar = translateColors.translateColors(player, actionBarData);
            player.sendActionBar(actionBar);
        }
    }

    public void sendTitle(Player player, int spawnId) {
        String titleData = titlesManager.getSpawnTitle(spawnId);
        if (!titleData.contains(";")) return;

        String[] parts = titleData.split(";", 2);
        Component titleComponent = translateColors.translateColors(player, parts[0]);
        Component subtitleComponent = translateColors.translateColors(player, parts.length > 1 ? parts[1] : "");

        Title title = Title.title(titleComponent, subtitleComponent, DEFAULT_TITLE_TIMES);
        player.showTitle(title);
    }

    public void sendMessage(Player player, int spawnId) {
        String message = spawnMessagesManager.getSpawnMessage(spawnId);
        if (!message.isEmpty()) {
            player.sendMessage(translateColors.translateColors(player, message));
        }
    }

    private record TargetInfo(Player target, boolean sendOtherPlayer, boolean sendAll) {}
}
