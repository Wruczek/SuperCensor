package mr.wruczek.supercensor3.utils;

import java.util.ArrayList;
import java.util.List;

import mr.wruczek.supercensor3.SCConfigManager2;
import mr.wruczek.supercensor3.utils.classes.SCLogger;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0
 * International License. http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class ConfigUtils {

    public static String getMessageFromMessagesFile(String path) {
        if (SCConfigManager2.messages.contains(path)) {
            return SCConfigManager2.messages.getColored(path);
        }

        if (SCConfigManager2.messages_original.contains(path)) {
            SCLogger.logWarning("Cannot find " + path + " in original messages.yml file, getting it from the jar file...");
            String original = StringUtils.color(SCConfigManager2.messages_original.getString(path));
            SCConfigManager2.messages.set(path, original);
            SCConfigManager2.messages.save();
            return original;
        }

        return SCUtils.getPluginPrefix() + StringUtils.color("&cFatal error: message \"" + path + "\" cannot be found!");
    }

    public static Object getFromConfig(String path, boolean bool) {
        if (SCConfigManager2.config.contains(path)) {
            return SCConfigManager2.config.get(path);
        }

        if (bool)
            return false;

        if (SCConfigManager2.config_original.contains(path)) {
            SCLogger.logWarning("Cannot find " + path + " in original config.yml file, getting it from the jar file...");
            Object original = SCConfigManager2.config_original.get(path);
            SCConfigManager2.config.set(path, original);
            SCConfigManager2.config.save();
            return original;
        }

        SCLogger.logError("Cannot find " + path + " in config.yml file");
        return null;
    }

    public static boolean configContains(String path) {
        return SCConfigManager2.config.contains(path);
    }

    public static String getStringFromConfig(String path) {
        return (String) getFromConfig(path, false);
    }

    public static String getColoredStringFromConfig(String path) {
        return StringUtils.color(getStringFromConfig(path));
    }

    @SuppressWarnings("unchecked")
    public static List<String> getStringListFromConfig(String path) {

        List<String> list = (List<String>) getFromConfig(path, false);

        if (list == null) {
            return new ArrayList<String>(0);
        }

        return list;
    }

    public static boolean getBooleanFromConfig(String path) {
        return (boolean) getFromConfig(path, true);
    }

    public static int getIntFromConfig(String path) {
        return (int) getFromConfig(path, false);
    }

    public static double getDoubleFromConfig(String path) {
        return (double) getFromConfig(path, false);
    }
}
