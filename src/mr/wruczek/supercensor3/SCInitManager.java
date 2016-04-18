package mr.wruczek.supercensor3;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import mr.wruczek.supercensor3.checks.AntiSpam;
import mr.wruczek.supercensor3.checks.SlowModeCheck;
import mr.wruczek.supercensor3.checks.SpecialCheck;
import mr.wruczek.supercensor3.checks.WordlistCheck;
import mr.wruczek.supercensor3.commands.SCCommandHeader;
import mr.wruczek.supercensor3.commands.SCMainCommand;
import mr.wruczek.supercensor3.commands.SCReportCommand;
import mr.wruczek.supercensor3.listeners.SCAsyncPlayerChatListener;
import mr.wruczek.supercensor3.listeners.SCPlayerJoinListener;
import mr.wruczek.supercensor3.listeners.SCPlayerQuitListener;
import mr.wruczek.supercensor3.utils.LoggerUtils;
import mr.wruczek.supercensor3.utils.classes.SCLogger;
import mr.wruczek.supercensor3.utils.classes.WordlistUpdater;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SCInitManager {

    public static void init() {

        SCMain instance = SCMain.getInstance();

        // region Utils
        SCConfigManager2.load();
        new SCCommandHeader();
        SCAutoMessage.run();

        new AntiSpam();

        Bukkit.getScheduler().runTaskAsynchronously(instance, new Runnable() {
            public void run() {
                try {
                    new WordlistUpdater().run();
                } catch (Exception e) {
                    SCLogger.logError("Exception occurred while running WordlistUpdater! " + e);
                    LoggerUtils.handleException(e);
                }
            }
        });
        // endregion

        // region Commands
        instance.getCommand("supercensor").setExecutor(new SCMainCommand());
        instance.getCommand("screport").setExecutor(new SCReportCommand());
        // endregion
    }

    public static void registerListeners() {

        PluginManager pluginManager = Bukkit.getPluginManager();
        SCMain instance = SCMain.getInstance();

        // region Listeners
        pluginManager.registerEvents(new SCPlayerJoinListener(), instance);
        pluginManager.registerEvents(new SCPlayerQuitListener(), instance);
        pluginManager.registerEvents(new SCAsyncPlayerChatListener(), instance);
        // enderegion

        // region Checks
        pluginManager.registerEvents(new SpecialCheck(), instance);
        pluginManager.registerEvents(new WordlistCheck(), instance);

        pluginManager.registerEvents(new AntiSpam(), instance);
        pluginManager.registerEvents(new SlowModeCheck(), instance);
        // endregion
    }

}
