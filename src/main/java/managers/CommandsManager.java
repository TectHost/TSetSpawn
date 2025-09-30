package managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

import java.util.*;

public class CommandsManager {
    private final TSetSpawn plugin;
    private final ConfigFile configFile;
    private final Map<Integer, List<String>> spawnCommands = new HashMap<>();

    public CommandsManager(TSetSpawn plugin) {
        this.plugin = plugin;
        this.configFile = new ConfigFile("commands.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadCommands();
    }

    private void loadCommands() {
        FileConfiguration config = configFile.getConfig();
        clearCommands();

        if (!config.contains("commands")) return;

        for (String key : Objects.requireNonNull(config.getConfigurationSection("commands")).getKeys(false)) {
            try {
                int spawnId = Integer.parseInt(key);
                List<String> commands = config.getStringList("commands." + key);
                spawnCommands.put(spawnId, commands);
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Invalid spawn ID in commands.yml: " + key);
            }
        }
    }

    public void reloadCommands() {
        configFile.reloadConfig();
        loadCommands();
    }

    public void clearCommands() {
        spawnCommands.clear();
    }

    public List<String> getCommandsForSpawn(int spawnId) {
        return spawnCommands.getOrDefault(spawnId, Collections.emptyList());
    }

    public void executeCommands(int spawnId, Player player) {
        List<String> commands = getCommandsForSpawn(spawnId);
        if (commands.isEmpty()) return;

        for (String command : commands) {
            String parsedCommand = command.replace("%player%", player.getName());

            if (parsedCommand.startsWith("[CONSOLE]")) {
                String consoleCommand = parsedCommand.replaceFirst("\\[CONSOLE]\\s*", "");
                Bukkit.getScheduler().runTask(plugin, () ->
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consoleCommand)
                );
            } else if (parsedCommand.startsWith("[PLAYER]")) {
                String playerCommand = parsedCommand.replaceFirst("\\[PLAYER]\\s*", "");
                Bukkit.getScheduler().runTask(plugin, () ->
                        player.performCommand(playerCommand)
                );
            }
        }
    }
}
