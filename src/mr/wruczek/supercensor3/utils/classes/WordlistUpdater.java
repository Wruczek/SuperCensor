package mr.wruczek.supercensor3.utils.classes;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import mr.wruczek.supercensor3.SCConfigManager2;
import mr.wruczek.supercensor3.SCMain;
import mr.wruczek.supercensor3.checks.CensorData;
import mr.wruczek.supercensor3.utils.ConfigUtils;
import mr.wruczek.supercensor3.utils.IOUtils;
import mr.wruczek.supercensor3.utils.LoggerUtils;
import mr.wruczek.supercensor3.utils.SCUtils;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class WordlistUpdater {

    private boolean enabled;
    private URL url;
    private File saveTo;
    private String prefix;

    public WordlistUpdater() {

        prefix = "[WordlistUpdater] ";
        enabled = true;

        if (getDataFromJarEmbeddedConfig()) {
            SCLogger.logInfo(prefix + "Loaded from Jar-Embedded config file.");
        } else if (getDataFromConfig()) {
            SCLogger.logInfo(prefix + "Loaded from config file.");
        } else {
            enabled = false;
            return;
        }

        if (!saveTo.exists()) {
            try {
                saveTo.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public WordlistUpdater(URL url) {
        this.url = url;
    }

    public void run() throws NoSuchAlgorithmException, IOException, ParseException {
        if (!enabled)
            return;

        SCLogger.logInfo(prefix + "Running WordlistUpdater...");

        String result = IOUtils.getContentFromURL(url);

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(result);

        String checksum = (String) jsonObject.get("checksum");

        JSONArray wordlist = (JSONArray) jsonObject.get("wordlist");

        FileConfiguration saveToConfig = YamlConfiguration.loadConfiguration(saveTo);

        if (checksum.equalsIgnoreCase(SCUtils.getListChecksum(saveToConfig.getStringList("Wordlist")))) {
            SCLogger.logInfo(prefix + "Checksum is identical, no need to update.");
            return;
        }

        SCLogger.logInfo(prefix + "Checksum is diffrent, updating!");

        saveToConfig.set("Info", "Updated " + LoggerUtils.getDate() + " at " + LoggerUtils.getTime() + " from url " + url);
        saveToConfig.set("Wordlist", wordlist);

        saveToConfig.save(saveTo);

        // load new file in
        CensorData.load(SCConfigManager2.rulesFolder);

        SCLogger.logInfo(prefix + "WordlistUpdater finished updating file.");
    }

    private boolean getDataFromConfig() {
        if (!ConfigUtils.getBooleanFromConfig("WordlistUpdater.Enabled"))
            return false;

        try {
            this.url = new URL(ConfigUtils.getStringFromConfig("WordlistUpdater.URL"));
            this.saveTo = new File(SCConfigManager2.rulesFolder, ConfigUtils.getStringFromConfig("WordlistUpdater.SaveTo"));
        } catch (MalformedURLException e) {
            SCLogger.logError(prefix + "The given url in WordlistUpdater is malformed. WordlistUpdater has been disabled.");
            return false;
        }

        return url != null && saveTo != null;
    }

    private boolean getDataFromJarEmbeddedConfig() {
        try {
            YamlConfiguration jarconfig = SCConfigManager2.configFromResource("wordlistupdater.yml");

            if (jarconfig.getBoolean("WordlistUpdater.Enabled") && jarconfig.contains("WordlistUpdater.URL") && jarconfig.contains("WordlistUpdater.SaveTo")) {
                this.url = new URL(jarconfig.getString("WordlistUpdater.URL"));
                this.saveTo = new File(SCConfigManager2.rulesFolder, jarconfig.getString("WordlistUpdater.SaveTo"));
            }
        } catch (MalformedURLException e) {
            SCLogger.logError(prefix + "The given url in Jar-Embedded WordlistUpdater is malformed. WordlistUpdater has been disabled.");
            return false;
        } catch (InvalidConfigurationException e) {
            SCLogger.logError(prefix + "Cannot load configuration from " + this.url);
            return false;
        }

        return url != null && saveTo != null;
    }
}
