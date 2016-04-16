package mr.wruczek.supercensor3;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import mr.wruczek.supercensor3.commands.subcommands.SCSelfMuteManager;
import mr.wruczek.supercensor3.utils.ConfigUtils;
import mr.wruczek.supercensor3.utils.LoggerUtils;
import mr.wruczek.supercensor3.utils.StringUtils;
import mr.wruczek.supercensor3.utils.classes.GravityUpdater.UpdateResult;
import mr.wruczek.supercensor3.utils.classes.SCLogger;
import mr.wruczek.supercensor3.utils.classes.SCUpdater;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SCMain extends JavaPlugin {

    private static SCMain instance;
    private static File pluginFile;

    @Override
    public void onEnable() {

        instance = this;

        long timerStart = System.currentTimeMillis();
        SCLogger.logInfo("Loading SuperCensor. Version: " + instance.getDescription().getVersion());

        pluginFile = instance.getFile();

        SCInitManager.init();
        SCInitManager.registerListeners();

        // region Display informations about messages file
        SCLogger.logInfo(ConfigUtils.getMessageFromMessagesFile("SystemEnable.MessagesLoaded")
                        .replace("%languagecode%", ConfigUtils.getMessageFromMessagesFile("LocalizationInformations.LanguageCode"))
                        .replace("%languagename%", ConfigUtils.getMessageFromMessagesFile("LocalizationInformations.Language")),
                LoggerUtils.LogType.PLUGIN);
        // endregion

        // region Auto-Updater
        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
            @Override
            public void run() {
                if (ConfigUtils.getBooleanFromConfig("AutoUpdater.CheckOnServerStartup")) {
                    UpdateResult result = SCUpdater.instance.getResult();

                    if (result == UpdateResult.SUCCESS) {
                        SCLogger.logInfo(StringUtils.formatUpdaterMessage(ConfigUtils
                                        .getMessageFromMessagesFile("Updater.ToConsole.Success")),
                                LoggerUtils.LogType.PLUGIN);
                    } else if (result == UpdateResult.UPDATE_AVAILABLE) {
                        SCLogger.logInfo(StringUtils.formatUpdaterMessage(ConfigUtils
                                        .getMessageFromMessagesFile("Updater.ToConsole.UpdateAvailable")),
                                LoggerUtils.LogType.PLUGIN);
                    } else if (result == UpdateResult.NO_UPDATE) {
                        SCLogger.logInfo(StringUtils.formatUpdaterMessage(ConfigUtils
                                        .getMessageFromMessagesFile("Updater.ToConsole.NoUpdate")),
                                LoggerUtils.LogType.PLUGIN);
                    }
                }
            }
        });
        // endregion

        try {
            // scPlayersDataManger = new SCPlayersDataManger(false, null, null, null, null, null);
        } catch (Exception e) {
            LoggerUtils.handleException(e);
        }

        long loadTime = System.currentTimeMillis() - timerStart;

        SCLogger.logInfo(ConfigUtils.getMessageFromMessagesFile("SystemEnable.Loaded")
                .replace("%time%", String.valueOf(loadTime)), LoggerUtils.LogType.PLUGIN);
    }

    @Override
    public void onDisable() {

        LoggerUtils.log("SuperCensor disabled", LoggerUtils.LogType.PLUGIN);

        SCSelfMuteManager.save();
        SCConfigManager2.data.save();
        try {
            // scPlayersDataManger.saveData();
        } catch (Exception e) {
            LoggerUtils.handleException(e);
        }
        // SCConfigManager.save();
    }

    public static SCMain getInstance() {
        return instance;
    }

    public static File getPluginFile() {
        return pluginFile;
    }
}
