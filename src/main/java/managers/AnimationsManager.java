package managers;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;
import utils.DebugLogger;
import utils.TranslateColors;

import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnimationsManager {
    private final TSetSpawn plugin;
    private final ConfigFile configFile;

    private final Map<Integer, List<String>> animations = new HashMap<>();

    private final TranslateColors translateColors;

    public AnimationsManager(TSetSpawn plugin) {
        this.plugin = plugin;
        this.configFile = new ConfigFile("animations.yml", "modules", plugin);
        this.configFile.registerConfig();
        this.translateColors = plugin.getTranslateColors();
        loadAnimations();
    }

    public void loadAnimations() {
        animations.clear();
        var config = configFile.getConfig();
        if (!config.isConfigurationSection("animations")) return;
        for (String spawnKey : Objects.requireNonNull(config.getConfigurationSection("animations")).getKeys(false)) {
            try {
                int spawnId = Integer.parseInt(spawnKey);
                List<String> lines = config.getStringList("animations." + spawnKey);
                animations.put(spawnId, lines);
            } catch (NumberFormatException ignored) {}
        }
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadAnimations();
    }

    public void playAnimation(Player player, int spawnId, Runnable onFinish) {
        List<String> animLines = animations.get(spawnId);
        if (animLines == null || animLines.isEmpty()) {
            onFinish.run();
            return;
        }
        new AnimationExecutor(player, animLines, onFinish).runTaskTimer(plugin, 0, 2);
    }

    private class AnimationExecutor extends BukkitRunnable {
        private final Player player;
        private final List<String> lines;

        private final Runnable onFinish;

        private final Deque<Boolean> ifStack = new ArrayDeque<>();

        private final Deque<ForContext> forStack = new ArrayDeque<>();

        private int index = 0;

        private final Map<String, Integer> localVariables = new HashMap<>();

        private int sleepTicks = 0;

        public AnimationExecutor(Player player, List<String> lines, Runnable onFinish) {
            this.player = player;
            this.lines = lines;
            this.onFinish = onFinish;
        }

        @Override
        public void run() {
            if (sleepTicks > 0) {
                sleepTicks--;
                return;
            }

            if (index >= lines.size()) {
                if (!forStack.isEmpty()) {
                    ForContext ctx = forStack.peek();
                    ctx.current++;
                    if (ctx.current <= ctx.end) {
                        localVariables.put(ctx.variable, ctx.current);
                        index = ctx.startLine + 1;
                    } else {
                        forStack.pop();
                        localVariables.remove(ctx.variable);
                        index++;
                    }
                    return;
                }

                ifStack.clear();
                onFinish.run();
                cancel();
                return;
            }

            String rawLine = lines.get(index).trim();
            String line = PlaceholderAPI.setPlaceholders(player, rawLine);
            line = replaceVariables(line);

            DebugLogger.log("[Animations] Executing line: " + line);

            if (!ifStack.isEmpty() && !ifStack.peek()) {
                if (line.matches("^\\[IF .+]")) {
                    ifStack.push(false);
                } else if (line.matches("^\\[ENDIF]$")) {
                    ifStack.pop();
                } else if (line.matches("^\\[ELSE]$")) {
                    ifStack.pop();
                    ifStack.push(true);
                }
                index++;
                return;
            }

            String type = getType(line);

            switch (type) {
                case "IF" -> {
                    handleIf(line);
                    index++;
                }
                case "ENDIF" -> {
                    if (!ifStack.isEmpty()) ifStack.pop();
                    DebugLogger.log("[Animations] [ENDIF] found, updated stack.");
                    index++;
                }
                case "ELSE" -> {
                    if (!ifStack.isEmpty()) {
                        boolean prev = ifStack.pop();
                        ifStack.push(!prev);
                        DebugLogger.log("[Animations] [ELSE] executed. New condition: " + !prev);
                    }
                    index++;
                }
                case "FOR" -> {
                    handleFor(line);
                    index++;
                }
                case "ENDFOR" -> {
                    if (!forStack.isEmpty()) {
                        ForContext ctx = forStack.peek();
                        ctx.current++;
                        if (ctx.current <= ctx.end) {
                            localVariables.put(ctx.variable, ctx.current);
                            index = ctx.startLine + 1;
                            return;
                        } else {
                            forStack.pop();
                            localVariables.remove(ctx.variable);
                        }
                    }
                    index++;
                }
                case "TITLE" -> {
                    handleTitle(line);
                    index++;
                }
                case "GM" -> {
                    handleGameMode(line);
                    index++;
                }
                case "MOVE" -> {
                    handleMove(line);
                    index++;
                }
                case "SOUND" -> {
                    handleSound(line);
                    index++;
                }
                case "CHAT" -> {
                    handleChat(line);
                    index++;
                }
                case "COMMAND" -> {
                    player.performCommand(line.substring(1));
                    index++;
                }
                case "SLEEP" -> {
                    handleSleep(line);
                    index++;
                }
                case "CONSOLE" -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), line.substring(1));
                    index++;
                }
                default -> {
                    DebugLogger.log("[Animations] Unknown line type: " + line);
                    index++;
                }
            }
        }

        private @NotNull String getType(@NotNull String line) {
            if (line.matches("^\\[IF .+")) return "IF";
            if (line.equals("[ENDIF]")) return "ENDIF";
            if (line.equals("[ELSE]")) return "ELSE";
            if (line.matches("^\\[FOR .+")) return "FOR";
            if (line.equals("[ENDFOR]")) return "ENDFOR";
            if (line.startsWith("[TITLE]")) return "TITLE";
            if (line.startsWith("[GM]")) return "GM";
            if (line.startsWith("[MOVE]")) return "MOVE";
            if (line.startsWith("[SOUND]")) return "SOUND";
            if (line.startsWith("[CHAT]")) return "CHAT";
            if (line.startsWith("[SLEEP]")) return "SLEEP";
            if (line.startsWith("[CONSOLE]")) return "CONSOLE";
            if (line.startsWith("/")) return "COMMAND";
            return "UNKNOWN";
        }

        private void handleSleep(@NotNull String line) {
            String arg = line.substring(7).trim();
            try {
                int seconds = Integer.parseInt(arg);
                sleepTicks = seconds * 20;
            } catch (NumberFormatException e) {
                DebugLogger.log("[Animations] Invalid [SLEEP] value: " + arg);
            }
        }

        private void handleIf(String line) {
            Pattern pattern = Pattern.compile("^\\[IF (.+) (==|!=|>=|<=|>|<) (.+)]$");
            Matcher matcher = pattern.matcher(line);
            if (!matcher.find()) {
                ifStack.push(false);
                return;
            }
            String left = matcher.group(1);
            String op = matcher.group(2);
            String right = matcher.group(3);

            left = PlaceholderAPI.setPlaceholders(player, left);
            right = PlaceholderAPI.setPlaceholders(player, right);

            left = replaceVariables(left);
            right = replaceVariables(right);

            boolean result = compareStrings(left, right, op);
            ifStack.push(result);
        }

        private void handleFor(String line) {
            Pattern pattern = Pattern.compile("^\\[FOR (\\w+) in (\\d+)\\.\\.(\\d+)]$");
            Matcher matcher = pattern.matcher(line);
            if (!matcher.find()) return;

            String var = matcher.group(1);
            int start = Integer.parseInt(matcher.group(2));
            int end = Integer.parseInt(matcher.group(3));

            forStack.push(new ForContext(var, start, end, index));
            localVariables.put(var, start);
        }


        private void handleTitle(@NotNull String line) {
            String content = line.substring(7).trim();
            String[] parts = content.split(";", 2);

            Component titleComp = translateColors.translateColors(player, parts[0]);
            Component subtitleComp = parts.length > 1 ? translateColors.translateColors(player, parts[1]) : Component.empty();

            player.showTitle(Title.title(titleComp, subtitleComp,
                    Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3500), Duration.ofMillis(1000))));
        }

        private void handleGameMode(@NotNull String line) {
            String modeStr = line.substring(4).trim();
            try {
                int mode = Integer.parseInt(modeStr);
                GameMode gm = GameMode.values()[mode];
                player.setGameMode(gm);
            } catch (Exception ignored) {
            }
        }

        private void handleMove(@NotNull String line) {
            String[] parts = line.substring(6).trim().split(" ");
            if (parts.length != 2) return;

            Location loc = player.getLocation();
            String axis = parts[0].toUpperCase();
            int amount;
            try {
                amount = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                return;
            }

            Location newLoc = loc.clone();
            switch (axis) {
                case "X" -> newLoc.setX(loc.getX() + amount);
                case "Y" -> newLoc.setY(loc.getY() + amount);
                case "Z" -> newLoc.setZ(loc.getZ() + amount);
                default -> {
                }
            }
            player.teleport(newLoc);
        }

        private void handleSound(@NotNull String line) {
            String[] parts = line.substring(7).trim().split(" ");
            if (parts.length < 1) return;

            try {
                Sound sound = Sound.valueOf(parts[0].toUpperCase());
                float volume = parts.length >= 2 ? Float.parseFloat(parts[1]) : 1f;
                float pitch = parts.length >= 3 ? Float.parseFloat(parts[2]) : 1f;
                player.playSound(player.getLocation(), sound, volume, pitch);
            } catch (IllegalArgumentException | NullPointerException ignored) {
            }
        }

        private void handleChat(@NotNull String line) {
            Component msg = translateColors.translateColors(player, line.substring(6).trim());
            player.sendMessage(msg);
        }

        private boolean compareStrings(String left, String right, String op) {
            Double leftNum = tryParseDouble(left);
            Double rightNum = tryParseDouble(right);

            if (leftNum != null && rightNum != null) {
                return switch (op) {
                    case "==" -> leftNum.equals(rightNum);
                    case "!=" -> !leftNum.equals(rightNum);
                    case ">" -> leftNum > rightNum;
                    case "<" -> leftNum < rightNum;
                    case ">=" -> leftNum >= rightNum;
                    case "<=" -> leftNum <= rightNum;
                    default -> false;
                };
            } else {
                return switch (op) {
                    case "==" -> left.equals(right);
                    case "!=" -> !left.equals(right);
                    default -> false;
                };
            }
        }

        @Contract(pure = true)
        private @Nullable Double tryParseDouble(String s) {
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        private String replaceVariables(String input) {
            for (var entry : localVariables.entrySet()) {
                input = input.replace("${" + entry.getKey() + "}", entry.getValue().toString());
            }
            return input;
        }

        private static class ForContext {
            String variable;
            int start;
            int end;
            int current;
            int startLine;

            ForContext(String variable, int start, int end, int startLine) {
                this.variable = variable;
                this.start = start;
                this.end = end;
                this.current = start;
                this.startLine = startLine;
            }
        }
    }
}
