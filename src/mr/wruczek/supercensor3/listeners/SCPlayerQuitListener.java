package mr.wruczek.supercensor3.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import mr.wruczek.supercensor3.checks.AntiSpam;
import mr.wruczek.supercensor3.checks.SCSlowModeManager;
import mr.wruczek.supercensor3.utils.ConfigUtils;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0
 * International License. http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SCPlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        changeQuitMessage(event);
        cleanup(event);
    }

    private void cleanup(PlayerQuitEvent event) {
        if (ConfigUtils.getBooleanFromConfig("SlowMode.AlwaysCleanupOnQuit")
                || SCSlowModeManager.getManager.getSlowModeTimeLeftInMillis(event.getPlayer()) <= 0)
            SCSlowModeManager.getManager.removeFromMap(event.getPlayer());

        AntiSpam.removePlayerFromMap(event.getPlayer());
    }

    private void changeQuitMessage(PlayerQuitEvent event) {
        if (ConfigUtils.getBooleanFromConfig("FunStuff.QuitMessage.Change")) {
            String message = ConfigUtils.getColoredStringFromConfig("FunStuff.QuitMessage.Message");

            if (message != null)
                message = message.replace("%nick%", event.getPlayer().getDisplayName());

            event.setQuitMessage(message);
        }
    }

}