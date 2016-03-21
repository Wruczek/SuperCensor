package mr.wruczek.supercensor3.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import mr.wruczek.supercensor3.commands.subcommands.SCMuteChatManager;
import mr.wruczek.supercensor3.commands.subcommands.SCSelfMuteManager;
import mr.wruczek.supercensor3.commands.subcommands.SubcommandClearchat;
import mr.wruczek.supercensor3.commands.subcommands.SubcommandHelp;
import mr.wruczek.supercensor3.commands.subcommands.SubcommandInfo;
import mr.wruczek.supercensor3.commands.subcommands.SubcommandPPM;
import mr.wruczek.supercensor3.commands.subcommands.SubcommandPerms;
import mr.wruczek.supercensor3.commands.subcommands.SubcommandReload;
import mr.wruczek.supercensor3.utils.ConfigUtils;
import mr.wruczek.supercensor3.utils.LoggerUtils;
import mr.wruczek.supercensor3.utils.SCUtils;
import mr.wruczek.supercensor3.utils.StringUtils;
import mr.wruczek.supercensor3.utils.classes.SCLogger;

public class SCMainCommand implements CommandExecutor, TabCompleter {

	private static HashMap<SCSubcommand, String[]> subcommands = new HashMap<SCSubcommand, String[]>();
	private static ArrayList<SCSubcommand> tabComplete = new ArrayList<>();

	public SCMainCommand() {
		new SubcommandHelp();
		new SubcommandReload();
		new SubcommandPerms();
		new SubcommandInfo();
		new SubcommandClearchat();
		new SubcommandPPM();
		new SCMuteChatManager();
		new SCSelfMuteManager();
	}
	
	public static HashMap<SCSubcommand, String[]> getSubcommands() {
		return subcommands;
	}

	public static ArrayList<SCSubcommand> getTabComplete() {
		return tabComplete;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(args.length < 1) {
			new SubcommandHelp().onCommand(sender, null, args);
			return false;
		}

		String commandName = args[0];
		
		SCSubcommand execute = null;
		
		searchLoop : for(Entry<SCSubcommand, String[]> entry : subcommands.entrySet()) {
			for(String cmd : entry.getValue()) {
				if(cmd.equalsIgnoreCase(commandName)) {
					execute = entry.getKey();
					break searchLoop;
				}
			}
		}
		
		if(execute != null) {
			try {
				execute.onCommand(sender, commandName, args);
			} catch (Exception e) {
				sender.sendMessage(SCUtils.getPluginPrefix() + StringUtils.color("&cAn unknown error "
						+ "occurred while attempting to perform this command: " + e));
				
				SCLogger.logError("An error occurred while attemping to perform command \""
						+ commandName + "\" with args \"" + args + "\"", LoggerUtils.LogType.PLUGIN);
				
				LoggerUtils.handleException(e);
			}
		} else {
			sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.SubcommandNotFound"));
		}

		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		SCSubcommand subcommand = null;
		
		if (args.length > 1) {
			for (SCSubcommand entry : tabComplete) {
				try {

					String[] aliases = subcommands.get(entry);

					for (String a : aliases) {
						if (a.equalsIgnoreCase(args[0])) {
							subcommand = entry;
							break;
						}
					}
				} catch (Exception e) {
					// Do nothing ¯\_(ツ)_/¯
				}
			}
		} else {
			subcommand = new SubcommandHelp();
		}
		
		List<String> ret = null;

		try {
			ret = subcommand.onTabComplete(sender, command, alias, args);
		} catch (Exception e) {
		}
		
		if (ret != null) {
			return ret;
		} else {
			// Return player list
			ArrayList<String> arrayToReturn = new ArrayList<String>();
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
					arrayToReturn.add(p.getName());
				}
			}
			Collections.sort(arrayToReturn);
			return arrayToReturn;
		}
	}

	public static void registerSubcommand(SCSubcommand command, String... aliases) {
		if (!subcommands.containsKey(command)) {
			subcommands.put(command, aliases);
		}
	}

	public static void registerTabCompletion(SCSubcommand command) {
		if (!tabComplete.contains(command)) {
			tabComplete.add(command);
		}
	}
	
}