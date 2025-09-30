package commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandsTabComplete implements TabCompleter {

    private final Utils utils;

    public CommandsTabComplete(Utils utils) {
        this.utils = utils;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String @NotNull [] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (utils.hasPerm(sender, "tss.reload")) completions.add("reload");
            if (utils.hasPerm(sender, "tss.version")) completions.add("version");
            if (utils.hasPerm(sender, "tss.menu")) completions.add("menu");
            if (utils.hasPerm(sender, "tss.config")) completions.add("config");

            String current = args[0].toLowerCase();
            completions.removeIf(s -> !s.startsWith(current));
            Collections.sort(completions);

        } else if (args.length == 2 && args[0].equalsIgnoreCase("config")) {
            List<String> keys = List.of(
                    "safe-location", "messages", "join", "void", "titles", "actionbar",
                    "permissions", "cooldown", "countdown", "sounds", "particles",
                    "fireworks", "respawn", "web", "gui", "spawn-list", "spawn-gui",
                    "commands", "animations", "bans", "debug", "lang-file", "prefix"
            );

            String current = args[1].toLowerCase();
            for (String key : keys) if (key.startsWith(current)) completions.add(key);
            Collections.sort(completions);

        } else if (args.length == 3 && args[0].equalsIgnoreCase("config")) {
            if (args[1].equalsIgnoreCase("debug") || args[1].equalsIgnoreCase("safe-location") || args[1].equalsIgnoreCase("messages")) {
                completions.add("true");
                completions.add("false");
            }
        }

        return completions;
    }
}
