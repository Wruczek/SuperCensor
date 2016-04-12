package mr.wruczek.supercensor3.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import mr.wruczek.supercensor3.SCCheckEvent;
import mr.wruczek.supercensor3.SCConfigManager2;
import mr.wruczek.supercensor3.SCMain;
import mr.wruczek.supercensor3.commands.SCMainCommand;
import mr.wruczek.supercensor3.commands.SCSubcommand;
import mr.wruczek.supercensor3.utils.ConfigUtils;
import mr.wruczek.supercensor3.utils.SCUtils;
import mr.wruczek.supercensor3.utils.StringUtils;
import mr.wruczek.supercensor3.utils.classes.SCPermissionsEnum;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SCMuteChatManager extends SCSubcommand implements Listener {

    public SCMuteChatManager() {
        SCMainCommand.registerSubcommand(this, "mute", "mutechat", "mc");
        SCMainCommand.registerTabCompletion(this);
        Bukkit.getPluginManager().registerEvents(this, SCMain.getInstance());
    }

    @EventHandler
    public void checkListener(SCCheckEvent event) {
        if (event.isCensored()
                || SCPermissionsEnum.MUTECHAT_EXEMPT.hasPermission(event.getPlayer())
                || !isChatMuted())
            return;

        if (SCMuteChatManager.isReasonSet())
            event.getPlayer().sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.MuteChat.CannotWriteReason")
                    .replace("%reason%", SCMuteChatManager.getReason()));
        else
            event.getPlayer().sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.MuteChat.CannotWrite"));

        event.setCensored(true);
    }

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {

        if (!SCUtils.checkPermissions(sender, SCPermissionsEnum.MUTECHAT_TOGGLE.toString())) {
            return;
        }

        boolean silentMode = args.length > 1 && args[1].equalsIgnoreCase("-s");

        if (isChatMuted()) {
            if (silentMode) {
                sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.MuteChat.ChatEnabled"));
            } else {
                Bukkit.broadcastMessage(ConfigUtils.getMessageFromMessagesFile("Commands.MuteChat.ChatEnabledBroadcast")
                        .replace("%nick%", sender.getName()));
            }
        } else {
            // With reason
            if (args.length > (silentMode ? 2 : 1)) {

                String reason = StringUtils.color(StringUtils.argsToString(args, silentMode ? 2 : 1));

                setReason(reason);

                if (silentMode) {
                    sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.MuteChat.ChatDisabledReason")
                            .replace("%reason%", reason));
                } else {
                    Bukkit.broadcastMessage(ConfigUtils.getMessageFromMessagesFile("Commands.MuteChat.ChatDisabledBroadcastReason")
                            .replace("%nick%", sender.getName())
                            .replace("%reason%", reason));
                }
            } else {

                setReason("");

                if (silentMode) {
                    sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.MuteChat.ChatDisabled"));
                } else {
                    Bukkit.broadcastMessage(ConfigUtils.getMessageFromMessagesFile(
                            "Commands.MuteChat.ChatDisabledBroadcast").replace("%nick%", sender.getName()));
                }
            }
        }

        setChatMuted(!isChatMuted());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> ret = new ArrayList<>();

        if (args.length == 2) {
            ret.add("-s");
        } else if (args.length == 3) {
            ret.add("[mute reason]");
        }

        return ret;
    }

    public static boolean isChatMuted() {
        return SCConfigManager2.data.getBoolean("GlobalChatMute.ChatMuted");
    }

    public static void setChatMuted(boolean muted) {
        SCConfigManager2.data.set("GlobalChatMute.ChatMuted", muted);
    }

    public static String getReason() {
        return SCConfigManager2.data.getColored("GlobalChatMute.Reason");
    }

    public static boolean isReasonSet() {
        return !getReason().isEmpty();
    }

    public static void setReason(String reason) {
        SCConfigManager2.data.set("GlobalChatMute.Reason", reason.replace(Character.toString('\u00A7'), String.valueOf(ChatColor.COLOR_CHAR)));
    }
}
