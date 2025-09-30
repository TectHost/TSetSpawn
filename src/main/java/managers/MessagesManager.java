package managers;

import org.bukkit.configuration.file.FileConfiguration;
import tsetspawn.TSetSpawn;
import utils.ConfigFile;

import java.util.List;

public class MessagesManager {

    private ConfigFile messagesFile;
    private final TSetSpawn plugin;

    private String onlyPlayer;
    private String invalidSpawn;
    private String notExist;
    private String invalidId;
    private String spawn;
    private String reload;
    private String version;
    private String noPerm;
    private String cooldown;
    private String countdown;
    private String TeleportSuccessOther;
    private String YouWereTeleported;
    private String playerNotFound;
    private String spawnDeleted;
    private String allSpawnsDeleted;
    private String alreadyExists;
    private String configUsage;
    private String configSet;
    private String configInvalidKey;
    private String configInvalidValue;
    private String listHeader;
    private String noSpawns;
    private String spawnList;
    private String safeLocation;
    private String update;
    private String banned;

    private String spawnbanUsageHeader;
    private String spawnbanUsageList;
    private String spawnbanUsageAdd;
    private String spawnbanUsageRemove;
    private String spawnbanUsageClear;

    private String spawnbanListUsage;
    private String spawnbanAddUsage;
    private String spawnbanRemoveUsage;
    private String spawnbanClearUsage;

    private String spawnbanInvalidId;
    private String spawnbanNoBans;
    private String spawnbanList;
    private String spawnbanPlayerNotFound;
    private String spawnbanAlreadyBanned;
    private String spawnbanBanned;
    private String spawnbanNotBanned;
    private String spawnbanUnbanned;
    private String spawnbanCleared;

    private String noMoney;
    private String paidMsg;

    private List<String> help;

    public MessagesManager(TSetSpawn plugin){
        this.messagesFile = new ConfigFile(plugin.getConfigManager().getLangFile(), "lang", plugin);
        this.plugin = plugin;
        this.messagesFile.registerConfig();
        loadConfig();
        generateAdditionalFiles();
    }

    public void loadConfig(){
        FileConfiguration config = messagesFile.getConfig();

        onlyPlayer = config.getString("messages.only-player");
        invalidSpawn = config.getString("messages.invalid-spawn");
        notExist = config.getString("messages.not-exist");
        invalidId = config.getString("messages.invalid-id");
        spawn = config.getString("messages.spawn");
        reload = config.getString("messages.reload");
        version = config.getString("messages.version");
        noPerm = config.getString("messages.no-perm");
        cooldown = config.getString("messages.cooldown");
        countdown = config.getString("messages.countdown");
        TeleportSuccessOther = config.getString("messages.teleport-success-other");
        YouWereTeleported = config.getString("messages.you-were-teleported");
        playerNotFound = config.getString("messages.player-not-found");
        spawnDeleted = config.getString("messages.spawn-deleted");
        allSpawnsDeleted = config.getString("messages.all-spawns-deleted");
        alreadyExists = config.getString("messages.already-exists");
        configUsage = config.getString("messages.config-usage");
        configSet = config.getString("messages.config-set");
        configInvalidKey = config.getString("messages.config-invalid-key");
        configInvalidValue = config.getString("messages.config-invalid-value");
        listHeader = config.getString("messages.list-header");
        noSpawns = config.getString("messages.no-spawns");
        spawnList = config.getString("messages.spawn-list");
        safeLocation = config.getString("messages.safe-location");
        update = config.getString("messages.update");
        banned = config.getString("messages.banned");

        if (plugin.getConfigManager().isBans()) {
            spawnbanUsageHeader = config.getString("spawnban.usage-header");
            spawnbanUsageList = config.getString("spawnban.usage-list");
            spawnbanUsageAdd = config.getString("spawnban.usage-add");
            spawnbanUsageRemove = config.getString("spawnban.usage-remove");
            spawnbanUsageClear = config.getString("spawnban.usage-clear");

            spawnbanListUsage = config.getString("spawnban.list-usage");
            spawnbanAddUsage = config.getString("spawnban.add-usage");
            spawnbanRemoveUsage = config.getString("spawnban.remove-usage");
            spawnbanClearUsage = config.getString("spawnban.clear-usage");

            spawnbanInvalidId = config.getString("spawnban.invalid-id");
            spawnbanNoBans = config.getString("spawnban.no-bans");
            spawnbanList = config.getString("spawnban.list");

            spawnbanPlayerNotFound = config.getString("spawnban.player-not-found");
            spawnbanAlreadyBanned = config.getString("spawnban.already-banned");
            spawnbanBanned = config.getString("spawnban.banned");

            spawnbanNotBanned = config.getString("spawnban.not-banned");
            spawnbanUnbanned = config.getString("spawnban.unbanned");
            spawnbanCleared = config.getString("spawnban.cleared");
        }

        noMoney = config.getString("economy.no-money-message");
        paidMsg = config.getString("economy.paid-message");

        help = config.getStringList("help");
    }

    public void reloadConfig(){
        this.messagesFile = new ConfigFile(plugin.getConfigManager().getLangFile(), "lang", plugin);
        messagesFile.reloadConfig();
        loadConfig();
    }

    public void generateAdditionalFiles() {
        createConfigFile("messages_es.yml");
        createConfigFile("messages_en.yml");
    }

    private void createConfigFile(String fileName) {
        ConfigFile configFile = new ConfigFile(fileName, "lang", plugin);
        configFile.registerConfig();
    }

    public List<String> getHelp() {return help;}
    public String getSpawnbanUsageHeader() {return spawnbanUsageHeader;}
    public String getSpawnbanUsageList() {return spawnbanUsageList;}
    public String getSpawnbanUsageAdd() {return spawnbanUsageAdd;}
    public String getSpawnbanUsageRemove() {return spawnbanUsageRemove;}
    public String getSpawnbanUsageClear() {return spawnbanUsageClear;}
    public String getSpawnbanListUsage() {return spawnbanListUsage;}
    public String getSpawnbanAddUsage() {return spawnbanAddUsage;}
    public String getSpawnbanRemoveUsage() {return spawnbanRemoveUsage;}
    public String getSpawnbanClearUsage() {return spawnbanClearUsage;}
    public String getSpawnbanInvalidId() {return spawnbanInvalidId;}
    public String getSpawnbanNoBans() {return spawnbanNoBans;}
    public String getSpawnbanList() {return spawnbanList;}
    public String getSpawnbanPlayerNotFound() {return spawnbanPlayerNotFound;}
    public String getSpawnbanAlreadyBanned() {return spawnbanAlreadyBanned;}
    public String getSpawnbanBanned() {return spawnbanBanned;}
    public String getSpawnbanNotBanned() {return spawnbanNotBanned;}
    public String getSpawnbanUnbanned() {return spawnbanUnbanned;}
    public String getSpawnbanCleared() {return spawnbanCleared;}
    public String getBanned() {return banned;}
    public String getUpdate() {return update;}
    public String getSafeLocation() {return safeLocation;}
    public String getListHeader() {return listHeader;}
    public String getNoSpawns() {return noSpawns;}
    public String getSpawnList() {return spawnList;}
    public String getOnlyPlayer() {return onlyPlayer;}
    public String getInvalidSpawn() {return invalidSpawn;}
    public String getNotExist() {return notExist;}
    public String getInvalidId() {return invalidId;}
    public String getSpawn() {return spawn;}
    public String getReload() {return reload;}
    public String getVersion() {return version;}
    public String getNoPerm() {return noPerm;}
    public String getCooldown() {return cooldown;}
    public String getCountdown() {return countdown;}
    public String getTeleportSuccessOther() {return TeleportSuccessOther;}
    public String getYouWereTeleported() {return YouWereTeleported;}
    public String getPlayerNotFound() {return playerNotFound;}
    public String getSpawnDeleted() {return spawnDeleted;}
    public String getAllSpawnsDeleted() {return allSpawnsDeleted;}
    public String getAlreadyExists() {return alreadyExists;}
    public String getConfigUsage() { return configUsage; }
    public String getConfigSet() { return configSet; }
    public String getConfigInvalidKey() { return configInvalidKey; }
    public String getConfigInvalidValue() { return configInvalidValue; }
    public String getNoMoney() {return noMoney;}
    public String getPaidMsg() {return paidMsg;}
}