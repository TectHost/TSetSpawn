package tsetspawn;

import commands.Commands;
import commands.SetSpawn;
import commands.Spawn;
import listeners.JoinListener;
import listeners.PlayerMoveListener;
import managers.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import utils.TranslateColors;

import java.util.Objects;

public final class TSetSpawn extends JavaPlugin {
    private TranslateColors translateColors;
    private ConfigManager configManager;
    private MessagesManager messagesManager;
    private SpawnsManager spawnsManager;
    private SpawnMessagesManager spawnMessagesManager;
    private DataManager dataManager;
    private JoinManager joinManager;
    private VoidManager voidManager;
    private TitlesManager titlesManager;
    private ActionBarManager actionBarManager;
    private Spawn spawn;
    private PermissionsManager permissionsManager;

    @Override
    public void onEnable() {
        getLogger().info("Starting TSetSpawn...");

        initializeManagers();
        loadConfigFiles();
        loadModules();
        loadCommands();

        getLogger().info("TSetSpawn Started!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Stopping TSetSpawn...");

        getSpawnsManager().clearSpawns();
        if (spawnMessagesManager != null) spawnMessagesManager.clearMessages();
        if (titlesManager != null) titlesManager.clearTitles();
        if (actionBarManager != null) actionBarManager.clearActionBar();
        if (permissionsManager != null) permissionsManager.clearPermissions();

        getLogger().warning("TSetSpawn Stopped!");
    }

    public void loadCommands() {
        Objects.requireNonNull(this.getCommand("tsetspawn")).setExecutor(new Commands(this));
        Objects.requireNonNull(this.getCommand("setspawn")).setExecutor(new SetSpawn(this, spawnsManager));
        Objects.requireNonNull(this.getCommand("spawn")).setExecutor(spawn);
    }

    public void initializeManagers() {
        translateColors = new TranslateColors();
    }

    public void loadConfigFiles() {
        configManager = new ConfigManager(this);
        messagesManager = new MessagesManager(this);
        spawnsManager = new SpawnsManager(this);
    }

    public void loadModules() {
        ConfigManager config = getConfigManager();
        PluginManager pluginManager = getServer().getPluginManager();

        if (config.isMessages()) spawnMessagesManager = new SpawnMessagesManager(this);
        if (config.isJoin()) {
            dataManager = new DataManager(this);
            joinManager = new JoinManager(this);
            pluginManager.registerEvents(new JoinListener(this), this);
        }
        if (config.isVoidModule()) {
            voidManager = new VoidManager(this);
            pluginManager.registerEvents(new PlayerMoveListener(this, spawn = new Spawn(this)), this);
        }
        if (config.isTitlesModule()) titlesManager = new TitlesManager(this);
        if (config.isActionBar()) actionBarManager = new ActionBarManager(this);
        if (config.isPermissions()) permissionsManager = new PermissionsManager(this);
    }

    public PermissionsManager getPermissionsManager() {
        return permissionsManager;
    }
    public ActionBarManager getActionBarManager() {
        return actionBarManager;
    }
    public TitlesManager getTitlesManager() {
        return titlesManager;
    }
    public VoidManager getVoidManager() {
        return voidManager;
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
    public DataManager getDataManager() {
        return dataManager;
    }
    public JoinManager getJoinManager() {
        return joinManager;
    }
}
