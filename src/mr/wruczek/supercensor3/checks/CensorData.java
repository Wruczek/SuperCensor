package mr.wruczek.supercensor3.checks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import mr.wruczek.supercensor3.utils.LoggerUtils;
import mr.wruczek.supercensor3.utils.classes.SCLogger;

// Small class created to keep all arrays loaded from rules folder

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class CensorData {

    private static Pattern hasNamePattern;

    public static Map<String, String> regexList;
    public static List<String> wordlist;
    public static List<String> whitelist;
    public static List<ConfigurationSection> special;
    // special

    public static void load(File folder) {

        regexList = new HashMap<>();
        wordlist = new ArrayList<>();
        whitelist = new ArrayList<>();
        special = new ArrayList<>();

        for (File file : folder.listFiles()) {
            String path = file.getAbsolutePath();

            String fileType = path.substring(path.lastIndexOf('.') + 1);

            if (fileType.equalsIgnoreCase("yml")) {
                try {
                    YamlConfiguration configFile = YamlConfiguration.loadConfiguration(new File(path));
                    wordlist.addAll(configFile.getStringList("Wordlist"));
                    whitelist.addAll(configFile.getStringList("Whitelist"));

                    ConfigurationSection specialCS = configFile.getConfigurationSection("Special");

                    if (specialCS != null)
                        special.add(specialCS);


                    SCLogger.logInfo("Loaded " + file.getName() + " rules file", LoggerUtils.LogType.PLUGIN);
                } catch (Exception e) {
                    SCLogger.logError("Error while loading " + path + ": " + e, LoggerUtils.LogType.PLUGIN);
                }
            } else if (fileType.equalsIgnoreCase("regex")) {
                try {
                    // region RegEx
                    FileInputStream fstream = new FileInputStream(path);
                    BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

                    String regex;
                    while ((regex = br.readLine()) != null) {
                        if (regex.startsWith("#") || regex.trim().isEmpty()) continue;

                        // if(hasName(regex)) {
                        String[] split = regex.split(": ", 2);
                        regexList.put(split[0].trim(), split[1].trim());
                        /* } else {
							regexList.add(regex);
						} */
                    }

                    br.close();

                    SCLogger.logInfo("Loaded " + file.getName() + " regex rules file", LoggerUtils.LogType.PLUGIN);
                    // endregion
                } catch (Exception e) {
                    SCLogger.logError("Error while loading Regex file " + path + ": " + e, LoggerUtils.LogType.PLUGIN);
                }
            }
        }
    }

    public String getRegexByName(String name) {
        for (Entry<String, String> entry : regexList.entrySet())
            if (entry.getKey().equalsIgnoreCase(name))
                return entry.getValue();

        return null;
    }

    private static boolean hasName(String str) {

        if (hasNamePattern == null)
            hasNamePattern = Pattern.compile("(([a-zA-Z0-9_]))(:)(\\s+)(\\S)",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

        return hasNamePattern.matcher(str).find();
    }
}
