package tsetspawn;

import commands.Commands;
import commands.SetSpawn;
import commands.Spawn;
import managers.ConfigManager;
import managers.MessagesManager;
import managers.SpawnMessagesManager;
import managers.SpawnsManager;
import org.bukkit.plugin.java.JavaPlugin;
import utils.TranslateColors;

import java.util.Objects;

public final class TSetSpawn extends JavaPlugin {
    private TranslateColors translateColors;
    private ConfigManager configManager;
    private MessagesManager messagesManager;
    private SpawnsManager spawnsManager;
    private SpawnMessagesManager spawnMessagesManager;

    @Override
    public void onEnable() {
        getLogger().info("Starting TSetSpawn...");

        loadConfigFiles();
        loadCommands();
        initializeManagers();

        getLogger().info("TSetSpawn Started!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Stopping TSetSpawn...");

        getSpawnsManager().clearSpawns();

        getLogger().warning("TSetSpawn Stopped!");
    }

    public void loadCommands() {
        Objects.requireNonNull(this.getCommand("tsetspawn")).setExecutor(new Commands(this));
        Objects.requireNonNull(this.getCommand("setspawn")).setExecutor(new SetSpawn(this, spawnsManager));
        Objects.requireNonNull(this.getCommand("spawn")).setExecutor(new Spawn(this));
    }

    public void initializeManagers() {
        translateColors = new TranslateColors();
    }

    public void loadConfigFiles() {
        configManager = new ConfigManager(this);
        messagesManager = new MessagesManager(this);
        spawnsManager = new SpawnsManager(this);
        spawnMessagesManager = new SpawnMessagesManager(this);
    }

    public TranslateColors getTranslateColors() {
        return translateColors;
    }
    public ConfigManager getConfigManager() {
        return configManager;
    }
    public MessagesManager getMessagesManager() {
        return messagesManager;
    }
    public SpawnsManager getSpawnsManager() {
        return spawnsManager;
    }
    public SpawnMessagesManager getSpawnMessagesManager() {
        return spawnMessagesManager;
    }
}
