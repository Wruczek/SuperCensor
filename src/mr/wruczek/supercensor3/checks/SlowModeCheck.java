package mr.wruczek.supercensor3.checks;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import mr.wruczek.supercensor3.SCCheckEvent;
import mr.wruczek.supercensor3.utils.ConfigUtils;
import mr.wruczek.supercensor3.utils.classes.SCPermissionsEnum;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SlowModeCheck implements Listener {

    @EventHandler
    public void checkListener(SCCheckEvent event) {

        if (event.isCensored() || !SCSlowModeManager.getManager.isEnabled())
            return;

        if (SCPermissionsEnum.SLOWMODE_BYPASS.hasPermission(event.getPlayer()))
            return;

        long timeInSeconds = SCSlowModeManager.getManager.getSlowModeTimeLeftInMillis(event.getPlayer()) / 1000;

        if (timeInSeconds > 0) {
            event.getPlayer().sendMessage(ConfigUtils.getMessageFromMessagesFile("SlowMode.MessageToPlayer").replace("%time%", String.valueOf(timeInSeconds)));
            event.setCensored(true);
            return;
        }

        SCSlowModeManager.getManager.addToMap(event.getPlayer());
    }
}
