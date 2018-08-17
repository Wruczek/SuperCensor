package mr.wruczek.supercensor3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import mr.wruczek.supercensor3.PPUtils.PPLoader;
import mr.wruczek.supercensor3.checks.CensorData;
import mr.wruczek.supercensor3.commands.subcommands.SCSelfMuteManager;
import mr.wruczek.supercensor3.utils.IOUtils;
import mr.wruczek.supercensor3.utils.LoggerUtils;
import mr.wruczek.supercensor3.utils.classes.SCConfig;
import mr.wruczek.supercensor3.utils.classes.SCLogger;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SCConfigManager2 {

    public static SCConfig config;
    public static SCConfig messages;
    public static SCConfig data;
    public static YamlConfiguration config_original;
    public static YamlConfiguration messages_original;
    public static File rulesFolder;
    public static File disabledRulesFolder;
    public static File logsFolder;
    public static File PPFolder;
    public static boolean freshlyInstalled;

    public static String pluginPrefix;

    public static boolean isInitialized() {
        return config != null;
    }

    public static void load() {

        freshlyInstalled = false;

        // region Logs
        logsFolder = new File(SCMain.getInstance().getDataFolder() + File.separator + "logs");

        if (!logsFolder.exists()) {
            logsFolder.mkdirs();
            freshlyInstalled = true;
        }
        // endregion

        // region Config File
        config = new SCConfig("config.yml");

        String messagesLanguage = config.getString("Language").replace("messages_", "").replace(".yml", "");

        if (messagesLanguage == null || messagesLanguage.trim().isEmpty()) {
            messagesLanguage = "en";
        }

        if (SCMain.getInstance().getResource("messages/messages_" + messagesLanguage + ".yml") == null) {
            SCLogger.logError("Cannot find \"messages_"
                    + messagesLanguage + "\" file! Changing to default \"messages_en.yml\"!", LoggerUtils.LogType.PLUGIN);
            messagesLanguage = "en";
        }

        if (!config.getString("Language").equals(messagesLanguage)) {
            SCLogger.logInfo("Saving corrected messages file...", LoggerUtils.LogType.PLUGIN);
            SCConfigManager2.config.set("Language", messagesLanguage);
            SCConfigManager2.save();
        }

        // endregion

        // region Messages File
        messages = new SCConfig("messages", "messages_" + messagesLanguage + ".yml");
        pluginPrefix = config.getColored("MessageFormat.PluginPrefix");
        // endregion

        // region Data File
        data = new SCConfig("data.yml");
        // endregion

        // region wordsloader
        rulesFolder = new File(SCMain.getInstance().getDataFolder() + File.separator + "rules");
        disabledRulesFolder = new File(rulesFolder + File.separator + "disabledFiles");

        if (!rulesFolder.exists()) {
            rulesFolder.mkdirs();
            try {
                IOUtils.copyResource("configs/Readme_rulesfolder.txt", new File(rulesFolder, "Readme.txt"), false);

                String dir = "examples";

                copyExampleFile(dir, rulesFolder, "exampleFile.yml");
                copyExampleFile(dir, rulesFolder, "specialExample.yml");
                copyExampleFile(dir, rulesFolder, "wordreplacerExample.yml");
                copyExampleFile(dir, rulesFolder, "regexExample.regex");
                copyExampleFile(dir, rulesFolder, "EnglishSwearWords.yml");
            } catch (IOException e) {
                SCLogger.logError("Cannot copy example rules files! " + e);
                LoggerUtils.handleException(e);
            }
        }

        // Load all files and arrays
        CensorData.load(rulesFolder);

        if (!disabledRulesFolder.exists()) {
            disabledRulesFolder.mkdirs();
            try {
                IOUtils.copyResource("configs/Readme_disabled_rulesfolder.txt", new File(disabledRulesFolder, "Readme.txt"), false);
            } catch (IOException e) {
                SCLogger.logError("Cannot copy Readme_disabled_rulesfolder.txt!");
                LoggerUtils.handleException(e);
            }
        }

        // endregion

        // region PenaltyPoints
        PPFolder = new File(SCMain.getInstance().getDataFolder() + File.separator + "PenaltyPointsRules");

        if (!PPFolder.exists()) {
            PPFolder.mkdirs();
            try {
                String dir = "ppr";

                copyExampleFile(dir, PPFolder, "warn-player.yml");
                copyExampleFile(dir, PPFolder, "kick-player.yml");
                copyExampleFile(dir, PPFolder, "mute-player.yml");
            } catch (IOException e) {
                SCLogger.logError("Cannot copy PenaltyPointsRules!");
                LoggerUtils.handleException(e);
            }
        }

        PPLoader.load(PPFolder);
        // endregion

        try {
            messages_original = configFromResource("messages/messages_en.yml");
            config_original = configFromResource("configs/config.yml");
        } catch (Exception e) {
            SCLogger.logError("Cannot load original messages files! " + e);
            LoggerUtils.handleException(e);
        }

        SCSelfMuteManager.load();
    }

    public static void save() {
        if (config != null)
            config.save();

        if (messages != null)
            messages.save();
    }

    private static void copyExampleFile(String fromDir, File toDir, String file) throws IOException {
        copyExampleFile(fromDir, toDir, file, file);
    }

    private static void copyExampleFile(String fromDir, File toDir, String source, String saveTo) throws IOException {
        IOUtils.copyResource("configs/" + fromDir + "/" + source, new File(toDir, saveTo), false);
    }

    public static YamlConfiguration configFromResource(String resourcePath) throws InvalidConfigurationException {
        InputStream is = SCMain.getInstance().getResource(resourcePath);

        if (is == null) {
            return null;
        }

        String config = null;

        // https://stackoverflow.com/a/5445161
        try (Scanner scanner = new Scanner(is)) {
            config = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : null;
        }

        if (config == null) {
            return null;
        }

        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.loadFromString(config);
        return yamlConfiguration;
    }
}
