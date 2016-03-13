package mr.wruczek.supercensor3.commands.subcommands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import mr.wruczek.supercensor3.commands.SCCommandHeader;
import mr.wruczek.supercensor3.commands.SCMainCommand;
import mr.wruczek.supercensor3.commands.SCSubcommand;
import mr.wruczek.supercensor3.utils.SCUtils;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SubcommandHelp extends SCSubcommand {
	
	public SubcommandHelp() {
		SCMainCommand.registerSubcommand(this, "help", "commands", "?");
		SCMainCommand.registerTabCompletion(this);
	}
	
	@Override
	public void onCommand(CommandSender sender, String command, String[] args) {
		
		sender.sendMessage(SCCommandHeader.getHeader());
		
		if(SCUtils.isTellrawSupported(sender)) {
			sender.sendMessage("\n");
		}
		
		SCUtils.sendCommandUsage(sender, "clear &8<&6own&8|&6all&8|&8<&6playername&8> [&6-a&8]", "Commands.ClearChat.HelpDescription");
		SCUtils.sendCommandUsage(sender, "mute &8[&6-s&8] &8[&6reason&8]", "Commands.MuteChat.HelpDescription");
		SCUtils.sendCommandUsage(sender, "selfmute &8[&6playername&8]", "Commands.SelfMute.HelpDescription");
		SCUtils.sendCommandUsage(sender, "ppm", "Commands.PPM.HelpDescription");
		SCUtils.sendCommandUsage(sender, "reload", "Commands.Reload.HelpDescription");
		SCUtils.sendCommandUsage(sender, "info", "Commands.Info.HelpDescription");
		SCUtils.sendCommandUsage(sender, "perms", "Commands.Permissions.HelpDescription");
		
		if(SCUtils.isTellrawSupported(sender)) {
			sender.sendMessage("\n" + SCUtils.getMessageFromMessagesFile("Commands.HoverCommandTip"));
		}
		
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return Arrays.asList("clear", "mute", "selfmute", "ppm", "reload", "info", "perms");
	}

}