package managers;

import org.bukkit.configuration.file.FileConfiguration;
import com.google.gson.JsonObject;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

public class ConfigManager {
    private final ConfigFile configFile;

    private boolean checkUpdates;

    private String langFile;
    private String prefix;
    private boolean safeLocation;

    private boolean join;
    private boolean messages;
    private boolean voidModule;
    private boolean titlesModule;
    private boolean actionBar;
    private boolean permissions;
    private boolean cooldown;
    private boolean countdown;
    private boolean sounds;
    private boolean particles;
    private boolean fireworks;
    private boolean respawn;
    private boolean web;
    private boolean gui;
    private boolean spawnList;
    private boolean spawnGui;
    private boolean commands;
    private boolean animations;
    private boolean bans;

    private boolean vault;

    private boolean debug;

    public ConfigManager(TSetSpawn plugin) {
        this.configFile = new ConfigFile("config.yml", null, plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = configFile.getConfig();

        checkUpdates = config.getBoolean("check-updates", true);

        langFile = config.getString("general.lang-file");
        prefix = config.getString("general.prefix");
        safeLocation = config.getBoolean("general.safe-location", true);

        join = config.getBoolean("modules.join", true);
        messages = config.getBoolean("modules.messages", true);
        voidModule = config.getBoolean("modules.void", true);
        titlesModule = config.getBoolean("modules.titles", true);
        actionBar = config.getBoolean("modules.actionbar", true);
        permissions = config.getBoolean("modules.permissions", false);
        cooldown = config.getBoolean("modules.cooldown", true);
        countdown = config.getBoolean("modules.countdown", true);
        sounds = config.getBoolean("modules.sounds", true);
        particles = config.getBoolean("modules.particles", true);
        fireworks = config.getBoolean("modules.fireworks", true);
        respawn = config.getBoolean("modules.respawn", true);
        web = config.getBoolean("modules.web", false);
        gui = config.getBoolean("modules.gui", false);
        spawnList = config.getBoolean("modules.spawn-list", true);
        spawnGui = config.getBoolean("modules.spawn-gui", false);
        commands = config.getBoolean("modules.commands", false);
        animations = config.getBoolean("modules.animations", true);
        bans = config.getBoolean("modules.bans", false);

        vault = config.getBoolean("hooks.vault", false);

        debug = config.getBoolean("debug", false);
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public void setConfig(String path, Object value) {
        configFile.getConfig().set(path, value);
        configFile.saveConfig();
    }

    public JsonObject getModulesAsJson() {
        JsonObject json = new JsonObject();
        json.addProperty("join", join);
        json.addProperty("messages", messages);
        json.addProperty("void", voidModule);
        json.addProperty("titles", titlesModule);
        json.addProperty("actionbar", actionBar);
        json.addProperty("permissions", permissions);
        json.addProperty("cooldown", cooldown);
        json.addProperty("countdown", countdown);
        json.addProperty("sounds", sounds);
        json.addProperty("particles", particles);
        json.addProperty("fireworks", fireworks);
        json.addProperty("respawn", respawn);
        json.addProperty("web", web);
        json.addProperty("gui", gui);
        json.addProperty("debug", debug);
        json.addProperty("lang-file", langFile);
        json.addProperty("spawn-list", spawnList);
        json.addProperty("spawn-gui", spawnGui);
        json.addProperty("safe-location", safeLocation);
        json.addProperty("vault", vault);
        json.addProperty("commands", commands);
        json.addProperty("prefix", prefix);
        json.addProperty("animations", animations);
        json.addProperty("check-updates", checkUpdates);
        json.addProperty("bans", bans);
        return json;
    }

    public boolean isCheckUpdates() {return checkUpdates;}

    public String getLangFile() {return langFile;}
    public String getPrefix() {return prefix;}
    public boolean isSafeLocation() {return safeLocation;}

    public boolean isBans() {return bans;}
    public boolean isAnimations() {return animations;}
    public boolean isSpawnGui() {return spawnGui;}
    public boolean isSpawnList() {return spawnList;}
    public boolean isJoin() {return join;}
    public boolean isMessages() {return messages;}
    public boolean isVoidModule() {return voidModule;}
    public boolean isTitlesModule() {return titlesModule;}
    public boolean isActionBar() {return actionBar;}
    public boolean isPermissions() {return permissions;}
    public boolean isCooldown() {return cooldown;}
    public boolean isCountdown() {return countdown;}
    public boolean isSounds() {return sounds;}
    public boolean isParticles() {return particles;}
    public boolean isFireworks() {return fireworks;}
    public boolean isRespawn() {return respawn;}
    public boolean isWeb() {return web;}
    public boolean isGui() {return gui;}
    public boolean isCommands() {return commands;}

    public boolean isVault() {return vault;}

    public boolean isDebug() {return debug;}
}
