package mr.wruczek.supercensor3.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mr.wruczek.supercensor3.SCConfigManager2;
import mr.wruczek.supercensor3.utils.classes.MessagesCreator;
import mr.wruczek.supercensor3.utils.classes.MessagesCreator.ChatExtra;
import mr.wruczek.supercensor3.utils.classes.Reflection;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class TellrawUtils {

    public static void sendCommandUsage(CommandSender sender, String command, String descriptionPath) {

        String description = descriptionPath;

        if(SCConfigManager2.messages.contains(descriptionPath) || SCConfigManager2.messages_original.contains(descriptionPath))
            description = ConfigUtils.getMessageFromMessagesFile(descriptionPath);

        if (isTellrawSupported(sender)) {
            String commandUsageFormat = ConfigUtils.getColoredStringFromConfig("MessageFormat.HelpEntryFormat");
            commandUsageFormat = StringUtils.color(commandUsageFormat.replace("%command%", command));

            sendTellraw((Player) sender, commandUsageFormat, description, StringUtils.unColor(commandUsageFormat.replace("- ", "")));
            return;
        }

        String commandUsageFormat = ConfigUtils.getColoredStringFromConfig("MessageFormat.OldHelpEntryFormat");

        sender.sendMessage(StringUtils.color(commandUsageFormat.replace("%command%", command).replace("%description%", description)));
    }

    public static void sendTellraw(Player player, String message, String hovertext) {
        TellrawUtils.sendTellraw(player, message, hovertext, "");
    }

    public static void sendTellraw(Player player, String message, String hovertext, String suggestedCommand) {
        MessagesCreator ms = new MessagesCreator("", null, null);
        ChatExtra extra = new MessagesCreator.ChatExtra(message, MessagesCreator.Color.WHITE, null);

        if (suggestedCommand != null)
            extra.setClickEvent(MessagesCreator.ClickEventType.SUGGEST_COMMAND, suggestedCommand);

        if (hovertext != null)
            extra.setHoverEvent(MessagesCreator.HoverEventType.SHOW_TEXT, hovertext);

        ms.addExtra(extra);

        try {
            Reflection.sendMessage(player, ms.toString());
        } catch (Exception e) {
            player.sendMessage(StringUtils.color(SCUtils.getPluginPrefix() + "Cannot send you formatted message. Please check console for full stacktrace. " + e));
            LoggerUtils.handleException(e);
        }
    }

    public static boolean isTellrawSupportedByServer() {
        String version = Bukkit.getBukkitVersion();
        return version.startsWith("1.9") || version.startsWith("1.8") || version.startsWith("1.7");
    }

    public static boolean isTellrawSupported(CommandSender sender) {
        return isTellrawSupportedByServer() && sender instanceof Player;
    }

}
