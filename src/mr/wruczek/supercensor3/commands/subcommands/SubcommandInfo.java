package mr.wruczek.supercensor3.commands.subcommands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import mr.wruczek.supercensor3.SCMain;
import mr.wruczek.supercensor3.checks.AntiSpam;
import mr.wruczek.supercensor3.checks.AntiSpamData;
import mr.wruczek.supercensor3.checks.CensorData;
import mr.wruczek.supercensor3.checks.SCSlowModeManager;
import mr.wruczek.supercensor3.commands.SCCommandHeader;
import mr.wruczek.supercensor3.commands.SCMainCommand;
import mr.wruczek.supercensor3.commands.SCSubcommand;
import mr.wruczek.supercensor3.utils.MessagesCreator;
import mr.wruczek.supercensor3.utils.MessagesCreator.ChatExtra;
import mr.wruczek.supercensor3.utils.Reflection;
import mr.wruczek.supercensor3.utils.SCLogger;
import mr.wruczek.supercensor3.utils.SCPermissionsEnum;
import mr.wruczek.supercensor3.utils.SCUpdater;
import mr.wruczek.supercensor3.utils.SCUtils;
import net.gravitydevelopment.updater.GravityUpdater.UpdateResult;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SubcommandInfo extends SCSubcommand {

	private static String pluginInfoJSON;
	private static HashMap<String, String> links;
	
	public SubcommandInfo() {
		SCMainCommand.registerSubcommand(this, "info", "informations", "about", "author");
		SCMainCommand.registerTabCompletion(this);
	}
	
	static {
		
		links = new HashMap<>();
		
		// TITLE, URL
		links.put(SCUtils.getMessageFromMessagesFile("Commands.Info.BungeeCord"), "Comming soon!");
		links.put(SCUtils.getMessageFromMessagesFile("Commands.Info.BugReporting"), "https://goo.gl/0H6qfY");
		links.put(SCUtils.getMessageFromMessagesFile("Commands.Info.BukkitDev"), "https://goo.gl/HCXb1p");
		links.put(SCUtils.getMessageFromMessagesFile("Commands.Info.SpigotMC"), "Comming soon!");
		links.put(SCUtils.getMessageFromMessagesFile("Commands.Info.GitHub"), "https://goo.gl/xvMzsJ");
		links.put(SCUtils.getMessageFromMessagesFile("Commands.Info.Donate"), "https://goo.gl/sY8Gvi");
		
		if(SCUtils.isTellrawSupportedByServer()) {
			pluginInfoJSON = generateJSONString(links);
		}
	}
	
	@Override
	public void onCommand(final CommandSender sender, String command, String[] args) {
		
		if(!SCUtils.checkForPermissions(sender, SCPermissionsEnum.INFO.toString())) {
			return;
		}
		
		if(args.length > 1) {
			sender.sendMessage("SuperCensor "
					+ SCMain.getInstance().getDescription().getVersion()
					+ " - Developer Informations");
			
			sender.sendMessage("Server version: " + Bukkit.getVersion());
			
			sender.sendMessage(" ");
			
			if(SCSlowModeManager.getManager.getMap().isEmpty())
				sender.sendMessage("Slowmode map: EMPTY!");
			
			for (Entry<String, Long> entry : SCSlowModeManager.getManager.getMap().entrySet())
				sender.sendMessage("* SCSlowModeManager.map: " + entry.getKey() + ": " + entry.getValue());
			
			sender.sendMessage(" ");
			
			sender.sendMessage("Loaded arrys from CensorData:");
			
			sender.sendMessage(" ");
			
			for (Entry<String, String> entry : CensorData.regexList.entrySet())
				sender.sendMessage("* CensorData.regexWithNames: " + 
						entry.getKey() + ": " + entry.getValue());
			
			sender.sendMessage(" ");
			
			for (String str : CensorData.wordlist)
				sender.sendMessage("* CensorData.wordlist: " + str);
			
			sender.sendMessage(" ");
			
			for (ConfigurationSection cs : CensorData.special)
				for(String key : cs.getKeys(false))
					sender.sendMessage("* CensorData.special: " + key);
			
			sender.sendMessage(" ");
			
			for (String str : CensorData.whitelist)
				sender.sendMessage("* CensorData.whitelist: " + str);
			
			sender.sendMessage(" ");
			
			for(AntiSpamData data : AntiSpam.getData())
				sender.sendMessage(data.toString());
			
			sender.sendMessage(" ");
			
			return;
		}
		
		Bukkit.getScheduler().runTaskAsynchronously(SCMain.getInstance(), new Runnable() {
			@Override
			public void run() {
				PluginDescriptionFile pdf = SCMain.getInstance().getDescription();
				
				String addToVersion = "";
				
				if(SCUpdater.instance.isUpdaterEnabled()) {
					
					sender.sendMessage(SCUtils.getMessageFromMessagesFile("Commands.Info.CheckingForUpdates"));
					
					UpdateResult result = SCUpdater.instance.checkForUpdates();
					
					if(result == UpdateResult.NO_UPDATE) {
						addToVersion = SCUtils.getMessageFromMessagesFile("Commands.Info.VersionStatus.UpToDate");
					} else if(result == UpdateResult.UPDATE_AVAILABLE) {
						addToVersion = SCUtils.getMessageFromMessagesFile("Commands.Info.VersionStatus.UpdateAvailable");
					} else if(result == UpdateResult.SUCCESS) {
						addToVersion = SCUtils.getMessageFromMessagesFile("Commands.Info.VersionStatus.NewVersionReady");
					}
				}
				
				sender.sendMessage(SCCommandHeader.getHeader());
				sender.sendMessage(SCUtils.getMessageFromMessagesFile("Commands.Info.Version") + pdf.getVersion() + addToVersion);
				sender.sendMessage(SCUtils.getMessageFromMessagesFile("Commands.Info.Author") + "Wruczek");
				
				sender.sendMessage("");
				sender.sendMessage(SCUtils.getMessageFromMessagesFile("Commands.Info.UsefulLinks"));
				
				if(SCUtils.isTellrawSupported(sender)) {
		            try {
		            	Reflection.sendMessage((Player) sender, pluginInfoJSON);
		            } catch (Exception e) {
		            	sender.sendMessage(SCUtils.color(SCUtils.getPluginPrefix() + "Cannot send you formatted message. Please check console for full stacktrace. " + e));
		            	SCLogger.handleException(e);
		            }
				} else {
					// For console / older Minecraft versions
					
					for(Entry<String, String> link : links.entrySet()) {
						sender.sendMessage(SCUtils.color("&7" + link.getKey() + ": &3" + link.getValue()));
					}
					
				}
			}
		});

	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return Arrays.asList("-dev");
	}
	
	private static String generateJSONString(HashMap<String, String> links) {
		
		StringBuilder sb = new StringBuilder("[");
		
		boolean insertSeperator = false;
		
		for(Entry<String, String> entry : links.entrySet()) {
			
			MessagesCreator ms = new MessagesCreator("", null, null);
			ChatExtra extra = new MessagesCreator.ChatExtra(entry.getKey(), MessagesCreator.Color.GOLD, null);
			String hovertext = entry.getValue();
			
			if(entry.getValue().startsWith("http")) {
				extra.setClickEvent(MessagesCreator.ClickEventType.OPEN_URL, entry.getValue());
				hovertext = "Click to visit the website";
			}
			
			extra.setHoverEvent(MessagesCreator.HoverEventType.SHOW_TEXT, hovertext);
			
			ms.addExtra(extra);
	        
			if(insertSeperator) {
				sb.append(getSeperator());
		        sb.append(",");
			} else {
				insertSeperator = true;
			}
			
	        sb.append(ms.toString());
	        sb.append(",");
		}
		
		sb.setLength(sb.length() - 1);
		sb.append("]");
		
		return sb.toString();
	}

	private static String getSeperator() {
		MessagesCreator ms = new MessagesCreator("", null, null);
		ms.addExtra(new MessagesCreator.ChatExtra(" - ", MessagesCreator.Color.DARK_GRAY, null));
		return ms.toString();
	}
	
}