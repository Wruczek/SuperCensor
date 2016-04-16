package mr.wruczek.supercensor3.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import mr.wruczek.supercensor3.SCConfigManager2;

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
        return UUIDUtils.getUUID(player, false);
    }

    public static String getUUID(String player, boolean dashes) {

        if (SCConfigManager2.config.getBoolean("General.UseUUID")) {
            try {
                UUID uuid = Bukkit.getOfflinePlayer(player).getUniqueId(); // TODO deprecated

                if (uuid == null || dashes) {
                    return uuid.toString();
                } else {
                    return uuid.toString().replace("-", "");
                }
            } catch (Exception e) {
                // Thrown mostly on servers below MC version 1.7.5
                // UUID API was implemented in this version.
            }
        }

        // If UUIDs are not supported or disabled,
        // we will return a nickname
        return player.toLowerCase();
    }
}
