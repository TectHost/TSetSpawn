package utils;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FireworksHandler {

    public static void launchFirework(Player player, FireworkEffect effect) {
        if (effect == null) return;

        Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(effect);
        meta.setPower(1);
        firework.setFireworkMeta(meta);
    }

    public static List<Color> parseColorList(@NotNull List<String> colorNames, Plugin plugin) {
        return colorNames.stream()
                .map(String::toUpperCase)
                .map(name -> parseColor(name, plugin))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static @Nullable Color parseColor(String name, Plugin plugin) {
        try {
            return (Color) Color.class.getField(name).get(null);
        } catch (Exception e) {
            plugin.getLogger().warning("Invalid firework color: " + name);
            return null;
        }
    }

    public static Type parseType(@NotNull String name, Plugin plugin) {
        try {
            return Type.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid firework type: " + name + ". Defaulting to BALL.");
            return Type.BALL;
        }
    }
}
