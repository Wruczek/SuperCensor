package mr.wruczek.supercensor3.commands.subcommands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import mr.wruczek.supercensor3.SCInitManager;
import mr.wruczek.supercensor3.commands.SCMainCommand;
import mr.wruczek.supercensor3.commands.SCSubcommand;
import mr.wruczek.supercensor3.utils.ConfigUtils;
import mr.wruczek.supercensor3.utils.LoggerUtils;
import mr.wruczek.supercensor3.utils.SCUtils;
import mr.wruczek.supercensor3.utils.classes.SCLogger;
import mr.wruczek.supercensor3.utils.classes.SCPermissionsEnum;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SubcommandReload extends SCSubcommand {

    public SubcommandReload() {
        SCMainCommand.registerSubcommand(this, "reload", "rl");
        SCMainCommand.registerTabCompletion(this);
    }

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {

        if (!SCUtils.checkPermissions(sender, SCPermissionsEnum.RELOAD.toString())) {
            return;
        }

        sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.Reload.Reloading"));

        try {
            long start = System.currentTimeMillis();

			/* unregister old listeners
			SCCheckEvent.getHandlerList();
			HandlerList.unregisterAll();
			SCUtils.logInfo("Old listeners unregistered");
			*/

            SCInitManager.init();

            long time = System.currentTimeMillis() - start;

            sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.Reload.Reloaded")
                    .replace("%time%", String.valueOf(time)));
        } catch (Exception e) {

            String reloadExceptionMessage = ConfigUtils.getMessageFromMessagesFile("Commands.Reload.Exception");

            if (reloadExceptionMessage != null) {
                sender.sendMessage(reloadExceptionMessage);
            } else {
                sender.sendMessage(SCUtils.getPluginPrefix() + ChatColor.RED + "An exception occurred while attemping to reload plugin! "
                        + "Please check console for more informations");
            }

            sender.sendMessage(ChatColor.RED + e.toString());

            SCLogger.logError("Exception while reloading plugin", LoggerUtils.LogType.PLUGIN);
            LoggerUtils.handleException(e);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Arrays.asList(); // no arguments
    }
}