package mr.wruczek.supercensor3.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import mr.wruczek.supercensor3.SCMain;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0
 * International License. http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class UUIDUtils {

    public static String getUUID(OfflinePlayer player) {
        return UUIDUtils.getUUID(player.getName());
    }

    public static String getUUID(String player) {
        return UUIDUtils.getUUID(player, false, false);
    }

    public static String getUUID(String player, boolean dashes, boolean async) {

        UUID uuid = null;

        try {

            if (async)
                uuid = SCMain.getUUIDCacher().getIdOptimistic(player);
            else
                uuid = SCMain.getUUIDCacher().getId(player);

        } catch (Exception e) {
            LoggerUtils.handleException(e);
        }

        try {
            if (uuid == null)
                uuid = Bukkit.getOfflinePlayer(player).getUniqueId();
        } catch (Exception e) {
        }

        if (dashes || uuid == null) {
            return uuid.toString();
        } else {
            return uuid.toString().replace("-", "");
        }
    }

}
