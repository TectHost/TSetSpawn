package tsetspawn;

import commands.*;
import hook.VaultHook;
import listeners.JoinListener;
import listeners.PlayerRespawnListener;
import listeners.PlayerMoveListener;
import listeners.QuitListener;
import managers.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import utils.*;

import java.util.List;
import java.util.Objects;

public final class TSetSpawn extends JavaPlugin {

    private String lastVersion = null;

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
    private Utils utils;
    private CooldownManager cooldownManager;
    private CooldownHandler cooldownHandler;
    private CountdownManager countdownManager;
    private CountdownHandler countdownHandler;
    private SoundsManager soundsManager;
    private ParticlesManager particlesManager;
    private FireworksManager fireworksManager;
    private RespawnManager respawnManager;
    private SpawnHttpServer httpServer;
    private WebManager webManager;
    private MenuGUI menuGUI;
    private MenuManager menuManager;
    private SpawnsGUI spawnsGUI;
    private SpawnListManager spawnListManager;
    private VaultManager vaultManager;
    private CommandsManager commandsManager;
    private AnimationsManager animationsManager;
    private BanManager banManager;

    @Override
    public void onEnable() {
        getLogger().info("Starting TSetSpawn...");

        loadConfigFiles();
        initializeManagers();
        loadModules();
        loadCommands();
        loadPlaceholders();

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
        if (cooldownHandler != null) cooldownHandler.clear();
        if (cooldownManager != null) cooldownManager.clear();
        if (countdownHandler != null) countdownHandler.cancelAllCountdowns();
        if (countdownManager != null) countdownManager.clearCountdowns();
        if (soundsManager != null) soundsManager.clearSounds();
        if (particlesManager != null) particlesManager.clearParticles();
        if (fireworksManager != null) fireworksManager.clear();
        if (httpServer != null) httpServer.stop();
        if (vaultManager != null) vaultManager.clearSpawns();
        if (commandsManager != null) commandsManager.clearCommands();
        if (banManager != null) banManager.clearBans();

        getLogger().warning("TSetSpawn Stopped!");
    }

    private void loadPlaceholders() {
        new Placeholders(this).register();
    }

    private void loadCommands() {
        Objects.requireNonNull(this.getCommand("tsetspawn")).setExecutor(new Commands(this));
        Objects.requireNonNull(this.getCommand("setspawn")).setExecutor(new SetSpawn(this, spawnsManager));
        Objects.requireNonNull(this.getCommand("spawn")).setExecutor(spawn);
        Objects.requireNonNull(this.getCommand("deletespawn")).setExecutor(new DeleteSpawnCommand(this));
    }

    private void initializeManagers() {
        translateColors = new TranslateColors(this);
        this.utils = new Utils(this);
        DebugLogger.init(this);
    }

    private void loadConfigFiles() {
        configManager = new ConfigManager(this);
        messagesManager = new MessagesManager(this);
        spawnsManager = new SpawnsManager(this);
    }

    private void loadModules() {
        ConfigManager config = getConfigManager();
        PluginManager pluginManager = getServer().getPluginManager();

        List<Runnable> moduleInitializers = List.of(
                () -> {
                    if (config.isVault()) {
                        vaultManager = new VaultManager(this);
                        VaultHook.setupEconomy(this);
                    }
                },
                () -> { if (config.isMessages()) spawnMessagesManager = new SpawnMessagesManager(this); },
                () -> {
                    if (config.isJoin()) {
                        dataManager = new DataManager(this);
                        joinManager = new JoinManager(this);
                        pluginManager.registerEvents(new JoinListener(this), this);
                        pluginManager.registerEvents(new QuitListener(this), this);
                    }
                },
                () -> { if (config.isTitlesModule()) titlesManager = new TitlesManager(this); },
                () -> { if (config.isActionBar()) actionBarManager = new ActionBarManager(this); },
                () -> { if (config.isPermissions()) permissionsManager = new PermissionsManager(this); },
                () -> {
                    if (config.isCooldown()) {
                        cooldownManager = new CooldownManager(this);
                        cooldownHandler = new CooldownHandler(cooldownManager);
                    }
                },
                () -> {
                    if (config.isCountdown()) {
                        countdownManager = new CountdownManager(this);
                        countdownHandler = new CountdownHandler(this);
                    }
                },
                () -> { if (config.isSounds()) soundsManager = new SoundsManager(this); },
                () -> { if (config.isParticles()) particlesManager = new ParticlesManager(this); },
                () -> { if (config.isFireworks()) fireworksManager = new FireworksManager(this); },
                () -> {
                    boolean spawnList = config.isSpawnList();
                    if (spawnList || config.isSpawnGui()) {
                        spawnListManager = new SpawnListManager(this);
                        spawnsGUI = new SpawnsGUI(this);
                        pluginManager.registerEvents(spawnsGUI, this);

                        if (spawnList) {
                            SpawnListCommand spawnListCommand = new SpawnListCommand(this);
                            Objects.requireNonNull(getCommand("spawnlist")).setExecutor(spawnListCommand);
                            if (spawnListManager.isGuiEnabled()) {
                                pluginManager.registerEvents(spawnListCommand, this);
                            }
                        }
                    }
                },
                () -> {
                    if (config.isCommands()) {
                        commandsManager = new CommandsManager(this);
                    }
                },
                () -> {
                    if (config.isAnimations()) {
                        animationsManager = new AnimationsManager(this);
                    }
                },
                () -> {
                    if (config.isBans()) {
                        banManager = new BanManager(this);
                        Objects.requireNonNull(getCommand("spawnban")).setExecutor(new BanSpawnCommand(this));
                    }
                },
                () -> spawn = new Spawn(this),
                () -> {
                    if (config.isVoidModule()) {
                        voidManager = new VoidManager(this);
                        pluginManager.registerEvents(new PlayerMoveListener(this, spawn), this);
                    }
                },
                () -> {
                    if (config.isRespawn()) {
                        respawnManager = new RespawnManager(this);
                        pluginManager.registerEvents(new PlayerRespawnListener(this, spawn), this);
                    }
                },
                () -> {
                    if (config.isWeb()) {
                        webManager = new WebManager(this);
                        SpawnDataService spawnDataService = new SpawnDataService(this);
                        HtmlManager htmlManager = new HtmlManager(this);

                        httpServer = new SpawnHttpServer(this, spawnDataService, htmlManager);
                        httpServer.start();
                    }
                },
                () -> {
                    if (config.isGui()) {
                        menuManager = new MenuManager(this);
                        menuGUI = new MenuGUI(this);
                        pluginManager.registerEvents(menuGUI, this);
                    }
                },
                () -> { if (config.isCheckUpdates()) CheckUpdates.check(getLogger(), getDescription().getVersion(), version -> lastVersion = version); }
        );

        moduleInitializers.forEach(Runnable::run);
    }

    public BanManager getBanManager() {return banManager;}
    public AnimationsManager getAnimationsManager() {return animationsManager;}
    public CommandsManager getCommandsManager() {return commandsManager;}
    public VaultManager getVaultManager() {return vaultManager;}
    public SpawnListManager getSpawnListManager() {return spawnListManager;}
    public SpawnsGUI getSpawnsGUI() {return spawnsGUI;}
    public MenuManager getMenuManager() {return menuManager;}
    public MenuGUI getMenuGUI() {return menuGUI;}
    public TranslateColors getTranslateColors() {return translateColors;}
    public ConfigManager getConfigManager() {return configManager;}
    public MessagesManager getMessagesManager() {return messagesManager;}
    public SpawnsManager getSpawnsManager() {return spawnsManager;}
    public SpawnMessagesManager getSpawnMessagesManager() {return spawnMessagesManager;}
    public DataManager getDataManager() {return dataManager;}
    public JoinManager getJoinManager() {return joinManager;}
    public VoidManager getVoidManager() {return voidManager;}
    public TitlesManager getTitlesManager() {return titlesManager;}
    public ActionBarManager getActionBarManager() {return actionBarManager;}
    public PermissionsManager getPermissionsManager() {return permissionsManager;}
    public Utils getUtils() {return utils;}
    public CooldownManager getCooldownManager() {return cooldownManager;}
    public CooldownHandler getCooldownHandler() {return cooldownHandler;}
    public CountdownManager getCountdownManager() {return countdownManager;}
    public CountdownHandler getCountdownHandler() {return countdownHandler;}
    public SoundsManager getSoundsManager() {return soundsManager;}
    public ParticlesManager getParticlesManager() {return particlesManager;}
    public FireworksManager getFireworksManager() {return fireworksManager;}
    public RespawnManager getRespawnManager() {return respawnManager;}
    public SpawnHttpServer getHttpServer() {return httpServer;}
    public WebManager getWebManager() {return webManager;}

    public String getLastVersion() {return lastVersion;}
}
