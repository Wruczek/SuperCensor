package mr.wruczek.supercensor3.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import mr.wruczek.supercensor3.SCCheckEvent;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SCAsyncPlayerChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        checkEventExecutor(event);
    }

    private void checkEventExecutor(AsyncPlayerChatEvent event) {
        if (event.isCancelled())
            return;

        // region SCCheckEvent executor
        SCCheckEvent checkEvent = new SCCheckEvent(event.getPlayer(), event.getMessage());

        Bukkit.getServer().getPluginManager().callEvent(checkEvent);

        if (checkEvent.isCensored())
            event.setCancelled(true);

        event.setMessage(checkEvent.getMessage());
        // endregion
    }

}