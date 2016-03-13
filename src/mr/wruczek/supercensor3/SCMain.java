package mr.wruczek.supercensor3;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import mr.wruczek.supercensor3.commands.subcommands.SCSelfMuteManager;
import mr.wruczek.supercensor3.data.SCPlayerDataManger;
import mr.wruczek.supercensor3.utils.SCLogger;
import mr.wruczek.supercensor3.utils.SCLogger.LogType;
import mr.wruczek.supercensor3.utils.SCUpdater;
import mr.wruczek.supercensor3.utils.SCUtils;
import net.gravitydevelopment.updater.GravityUpdater.UpdateResult;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SCMain extends JavaPlugin {

	private static SCMain instance;
	public static File pluginFile;
	public static SCPlayerDataManger scPlayersDataManger;
	
	@Override
	public void onEnable() {
		
		instance = this;
		
		long timerStart = System.currentTimeMillis();
		SCUtils.logInfo("Loading SuperCensor. Version: " + instance.getDescription().getVersion());
		
		pluginFile = instance.getFile();
		
		SCInitManager.init();
		SCInitManager.registerListeners();
		
		// region Display informations about messages file
		SCUtils.logInfo(SCUtils.getMessageFromMessagesFile("SystemEnable.MessagesLoaded")
				.replace("%languagecode%", SCUtils.getMessageFromMessagesFile("LocalizationInformations.LanguageCode"))
				.replace("%languagename%", SCUtils.getMessageFromMessagesFile("LocalizationInformations.Language")),
				LogType.PLUGIN);
		// endregion
		
		// region Auto-Updater
		Bukkit.getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
			@Override
			public void run() {
				if(SCUtils.getBooleanFromConfig("AutoUpdater.CheckOnServerStartup")) {
					UpdateResult result = SCUpdater.instance.getResult();
					
					if (result == UpdateResult.SUCCESS) {
						SCUtils.logInfo(SCUtils.formatUpdaterMessage(SCUtils
										.getMessageFromMessagesFile("Updater.ToConsole.Success")),
								LogType.PLUGIN);
					} else if (result == UpdateResult.UPDATE_AVAILABLE) {
						SCUtils.logInfo(SCUtils.formatUpdaterMessage(SCUtils
										.getMessageFromMessagesFile("Updater.ToConsole.UpdateAvailable")),
								LogType.PLUGIN);
					} else if (result == UpdateResult.NO_UPDATE) {
						SCUtils.logInfo(SCUtils.formatUpdaterMessage(SCUtils
										.getMessageFromMessagesFile("Updater.ToConsole.NoUpdate")),
								LogType.PLUGIN);
					}
				}
			}
		});
		// endregion
		
		try {
			// scPlayersDataManger = new SCPlayersDataManger(false, null, null, null, null, null);
		} catch (Exception e) {
			SCLogger.handleException(e);
		}
		
		long loadTime = System.currentTimeMillis() - timerStart;
		
		SCUtils.logInfo(SCUtils.getMessageFromMessagesFile("SystemEnable.Loaded")
				.replace("%time%", String.valueOf(loadTime)), LogType.PLUGIN);
	}
	
	@Override
	public void onDisable() {
		
		SCLogger.log("SuperCensor disabled", LogType.PLUGIN);
		
		SCSelfMuteManager.save();
		SCConfigManager2.data.save();
		try {
			// scPlayersDataManger.saveData();
		} catch (Exception e) {
			SCLogger.handleException(e);
		}
		// SCConfigManager.save();
	}
	
	public static SCMain getInstance() {
		return instance;
	}
	
}