package utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;

public class DebugLogger {

    private static boolean enabled = false;
    private static TSetSpawn plugin;

    public static void init(@NotNull TSetSpawn pl) {
        plugin = pl;
        enabled = plugin.getConfigManager().isDebug();
    }

    public static void log(String message) {
        if (enabled) {
            plugin.getLogger().info(format(message));
        }
    }

    public static void log(Object... parts) {
        if (!enabled) return;
        plugin.getLogger().info(format(join(parts)));
    }

    public static void warn(String message) {
        if (enabled) {
            plugin.getLogger().warning(format(message));
        }
    }

    public static void warn(Object... parts) {
        if (!enabled) return;
        plugin.getLogger().warning(format(join(parts)));
    }

    public static void error(String context, Throwable t) {
        if (!enabled) return;
        plugin.getLogger().severe(format(context + ": " + t.getClass().getSimpleName() + " - " + t.getMessage()));
        for (StackTraceElement element : t.getStackTrace()) {
            plugin.getLogger().severe("  at " + element.toString());
        }
    }

    private static @NotNull String join(Object @NotNull ... parts) {
        StringBuilder builder = new StringBuilder();
        for (Object part : parts) {
            builder.append(part).append(" ");
        }
        return builder.toString().trim();
    }

    @Contract(pure = true)
    private static @NotNull String format(String msg) {
        return "[DEBUG] " + msg;
    }
}
