package mr.wruczek.supercensor3;

import java.io.File;
import java.io.IOException;

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
	
	public static String pluginPrefix;
	
	public static boolean isInitialized() {
		return config != null;
	}
	
	public static void load() {
		
		// region Logs
		logsFolder = new File(SCMain.getInstance().getDataFolder() + File.separator + "logs");

		if (!logsFolder.exists())
			logsFolder.mkdirs();
		// endregion
		
		// region Config File
		config = new SCConfig("config.yml");
		
		String messagesLanguage = config.getString("Language").replace("messages_", "").replace(".yml", "");
		
		if(messagesLanguage == null || messagesLanguage.trim().isEmpty()) {
			messagesLanguage = "en";
		}
		
		if (SCMain.getInstance().getResource("messages/messages_" + messagesLanguage + ".yml") == null) {
			SCLogger.logError("Cannot find \"messages_"
					+ messagesLanguage + "\" file! Changing to default \"messages_en.yml\"!", LoggerUtils.LogType.PLUGIN);
			messagesLanguage = "en";
		}
		
		if(!config.getString("Language").equals(messagesLanguage)) {
			SCLogger.logInfo("Saving corrected messages file...", LoggerUtils.LogType.PLUGIN);
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
				
				// EXAMPLE CONFIG FILES
				File exampleFile = new File(rulesFolder, "exampleFile.yml");
				IOUtils.copyResource("configs/examples/exampleFile.yml", exampleFile, false);
				// We dont want to comments go away after save()
				// exampleFile.setReadOnly();
				
				File specialExample = new File(rulesFolder, "specialExample.yml");
				IOUtils.copyResource("configs/examples/specialExample.yml", specialExample, false);
				// We dont want to comments go away after save()
				// specialExample.setReadOnly();
				
				File wordreplacerExample = new File(rulesFolder, "wordreplacerExample.yml");
				IOUtils.copyResource("configs/examples/wordreplacerExample.yml", wordreplacerExample, false);
				// We dont want to comments go away after save()
				// wordreplacerExample.setReadOnly();
				
				File regexExample = new File(rulesFolder, "regexExample.regex");
				IOUtils.copyResource("configs/examples/regexExample.regex", regexExample, false);
				// We dont want to comments go away after save()
				// regexExample.setReadOnly();
			} catch (IOException e) {
				// ¯\_(ツ)_/¯
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
				// WARN PLAYER
				IOUtils.copyResource("configs/ppr/warn-player.yml", new File(PPFolder, "warn-player.yml"), false);
				
				// KICK PLAYER
				IOUtils.copyResource("configs/ppr/kick-player.yml", new File(PPFolder, "kick-player.yml"), false);
				
				// MUTE PLAYER
				IOUtils.copyResource("configs/ppr/mute-player.yml", new File(PPFolder, "mute-player.yml"), false);
			} catch (IOException e) {
				SCLogger.logError("Cannot copy PenaltyPointsRules!");
				LoggerUtils.handleException(e);
			}
		}

		PPLoader.load(PPFolder);
		// endregion
		
		try {
			messages_original = YamlConfiguration.loadConfiguration(SCMain.getInstance().getResource("messages/messages_en.yml"));
			config_original = YamlConfiguration.loadConfiguration(SCMain.getInstance().getResource("configs/config.yml"));
		} catch (Exception e) {
			SCLogger.logError("Cannot load original messages files! " + e);
			LoggerUtils.handleException(e);
		}
		
		SCSelfMuteManager.load();
	}
	
	public static void save() {
		config.save();
		messages.save();
	}
}