package listeners;

import managers.DataManager;
import managers.JoinManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;
import utils.TranslateColors;

public class QuitListener implements Listener {

    private final JoinManager joinManager;
    private final TranslateColors translateColors;
    private final DataManager dataManager;

    @Contract(pure = true)
    public QuitListener(@NotNull TSetSpawn plugin) {
        this.joinManager = plugin.getJoinManager();
        this.translateColors = plugin.getTranslateColors();
        this.dataManager = plugin.getDataManager();
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String message = joinManager.getQuit();

        if (!message.isEmpty()) {
            event.quitMessage(translateColors.translateColors(player, message));
        }

        dataManager.setJoined(player.getUniqueId());
    }
}
