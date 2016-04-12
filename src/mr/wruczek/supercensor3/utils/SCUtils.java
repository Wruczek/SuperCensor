package mr.wruczek.supercensor3.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SCUtils {

    public static String getPluginPrefix() {
        return StringUtils.color("&8[&6SC&8]&7 ");
    }

    public static String getLogPrefix() {
        return StringUtils.unColor(getPluginPrefix());
    }

    public static boolean checkPermissions(CommandSender sender, String permission) {
        if (sender.hasPermission(permission)) {
            return true;
        }

        sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.NoPermissions").replace("%permission%", permission));
        return false;
    }

    public static String getCommandDescription(String descriptionPath) {
        return ConfigUtils.getMessageFromMessagesFile("Commands.Description") + ConfigUtils.getMessageFromMessagesFile(descriptionPath);
    }

    // This is simple workaround over 1.8 changes.
    public static int getNumberOfPlayersOnline() {
        int i = 0;
        for (@SuppressWarnings("unused") Player player : Bukkit.getOnlinePlayers())
            i++;
        return i;
    }

    public static String getMD5Checksum(String string) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(string.getBytes());
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    public static String getListChecksum(List<String> list) throws NoSuchAlgorithmException {
        StringBuilder sb = new StringBuilder();

        for (String entry : list)
            sb.append(entry);

        return getMD5Checksum(sb.toString());
    }

}