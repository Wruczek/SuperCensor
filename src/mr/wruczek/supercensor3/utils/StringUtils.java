package mr.wruczek.supercensor3.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

import mr.wruczek.supercensor3.SCConfigManager2;
import mr.wruczek.supercensor3.SCMain;
import mr.wruczek.supercensor3.utils.classes.GravityUpdater;
import mr.wruczek.supercensor3.utils.classes.SCUpdater;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0
 * International License. http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class StringUtils {

    /**
     * @see ChatColor#translateAlternateColorCodes(char, String)
     */
    public static String color(String str) {
        if (str == null)
            return null;

        if (SCConfigManager2.isInitialized() && SCConfigManager2.pluginPrefix != null) {
            str = str.replace("%prefix%", SCConfigManager2.pluginPrefix);
        }

        return ChatColor.translateAlternateColorCodes('&', str);
    }

    /**
     * @see ChatColor#stripColor(String)
     */
    public static String unColor(String str) {
        return ChatColor.stripColor(str);
    }

    public static String formatUpdaterMessage(String message) {
        GravityUpdater updater = SCUpdater.instance.getUpdater();

        if (updater == null)
            return null;

        return message.replace("%updaterprefix%", ConfigUtils.getColoredStringFromConfig("MessageFormat.UpdaterPrefix"))
                .replace("%currentversion%", SCMain.getInstance().getDescription().getFullName())
                .replace("%newversion%", updater.getLatestName())
                .replace("%downloadlink%", updater.getLatestFileLink());
    }

    public static String usageFormatter(String command, String... args) {
        if (command == null || args == null)
            return null;

        String prefix = ConfigUtils.getColoredStringFromConfig("MessageFormat.CommandUsageFormat").replace("%command%",
                command);

        StringBuilder usageBuilder = new StringBuilder();

        for (String str : args) {
            if (str.startsWith("!")) {
                usageBuilder.append("&8[&6" + str.substring(1) + "&8] ");
            } else {
                usageBuilder.append("&8<&6" + str + "&8> ");
            }
        }

        String usage = usageBuilder.toString();
        usage = usage.substring(0, usage.length() - 1);

        return StringUtils.color(prefix.replace("%usage%", usage));
    }

    public static String argsToString(String[] args, int startFrom) {

        if (args.length == 0) {
            return "";
        }

        String ret = "";

        for (int i = startFrom; i < args.length; i++) {
            ret += args[i] + " ";
        }

        return ret.substring(0, ret.length() - 1);
    }

    /**
     * Credits: http://stackoverflow.com/a/12026782
     */
    public static String replaceIgnoreCase(String source, String target, String replacement) {
        StringBuilder sbSource = new StringBuilder(source);
        StringBuilder sbSourceLower = new StringBuilder(source.toLowerCase());
        String searchString = target.toLowerCase();

        int idx = 0;
        while ((idx = sbSourceLower.indexOf(searchString, idx)) != -1) {
            sbSource.replace(idx, idx + searchString.length(), replacement);
            sbSourceLower.replace(idx, idx + searchString.length(), replacement);
            idx += replacement.length();
        }
        sbSourceLower.setLength(0);
        sbSourceLower.trimToSize();
        sbSourceLower = null;

        return sbSource.toString();
    }

    public static String checkRegex(String regex, String string, boolean extraPrecautions) {
        String specialCharsRegex = "[^!@#$%^&*-.]*";
        String newregex = specialCharsRegex + "(" + regex + ")" + specialCharsRegex;

        String enchantedRegex = checkRegex(newregex, string);

        if (!extraPrecautions || enchantedRegex == null) {
            return checkRegex(regex, string);
        } else {
            return enchantedRegex;
        }
    }

    private static String checkRegex(String regex, String string) {
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(string);

        while (matcher.find()) {
            // System.out.println(matcher.group(0));
            return matcher.group(0);
        }

        return null;
    }

    public static double getCapsPercent(String str) {
        int total = 0;
        int uppers = 0;

        for (char c : str.toCharArray()) {

            if (!Character.isAlphabetic(c))
                continue;

            total++;

            if (Character.isUpperCase(c)) {
                uppers++;
            }
        }

        if (total == 0 && uppers == 0)
            return 0D;

        return (uppers * 1D) / (total * 1D) * 100D;
    }
}
