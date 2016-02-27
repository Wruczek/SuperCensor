package mr.wruczek.supercensor3.commands.subcommands;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginDescriptionFile;

import mr.wruczek.supercensor3.SCMain;
import mr.wruczek.supercensor3.checks.AntiSpam;
import mr.wruczek.supercensor3.checks.AntiSpamData;
import mr.wruczek.supercensor3.checks.CensorData;
import mr.wruczek.supercensor3.checks.SCSlowModeManager;
import mr.wruczek.supercensor3.commands.SCCommandHeader;
import mr.wruczek.supercensor3.commands.SCMainCommand;
import mr.wruczek.supercensor3.commands.SCSubcommand;
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

	public SubcommandInfo() {
		SCMainCommand.registerSubcommand(this, "info", "informations", "about", "author");
		SCMainCommand.registerTabCompletion(this);
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
					// Later will be changed to reflections
					String json = "[\"\",{\"text\":\"" + SCUtils.getMessageFromMessagesFile("Commands.Info.BungeeCord") + "\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Comming soon!\"}]}}},{\"text\":\" - \",\"color\":\"dark_gray\"},{\"text\":\"" + SCUtils.getMessageFromMessagesFile("Commands.Info.BugReporting") + "\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://goo.gl/0H6qfY\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Click here to visit the website\"}]}}},{\"text\":\" - \",\"color\":\"dark_gray\"},{\"text\":\"" + SCUtils.getMessageFromMessagesFile("Commands.Info.BukkitDev") + "\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://goo.gl/HCXb1p\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"\"},{\"text\":\"Click here to visit the website!\"}]}}},{\"text\":\" - \",\"color\":\"dark_gray\"},{\"text\":\"" + SCUtils.getMessageFromMessagesFile("Commands.Info.SpigotMC") + "\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Comming soon!\"}]}}},{\"text\":\" - \",\"color\":\"dark_gray\"},{\"text\":\"" + SCUtils.getMessageFromMessagesFile("Commands.Info.GitHub") + "\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://goo.gl/aU0LUw\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Click here to visit the website!\"}]}}},{\"text\":\" - \",\"color\":\"dark_gray\"},{\"text\":\"" + SCUtils.getMessageFromMessagesFile("Commands.Info.Donate") + "\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://goo.gl/sY8Gvi\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Click here to support developer\"}]}}}]";
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:tellraw " + sender.getName() + " " + json);
				} else {
					// For console / older Minecraft versions
					sendMessage(sender, "&7" + SCUtils.getMessageFromMessagesFile("Commands.Info.BungeeCord") + ": &3Comming soon!");
					sendMessage(sender, "&7" + SCUtils.getMessageFromMessagesFile("Commands.Info.BugReporting") + ": &3http://goo.gl/0H6qfY");
					sendMessage(sender, "&7" + SCUtils.getMessageFromMessagesFile("Commands.Info.BukkitDev") + ": &3http://goo.gl/HCXb1p");
					sendMessage(sender, "&7" + SCUtils.getMessageFromMessagesFile("Commands.Info.SpigotMC") + ": &3Comming soon!");
					sendMessage(sender, "&7" + SCUtils.getMessageFromMessagesFile("Commands.Info.GitHub") + ": &3http://goo.gl/aU0LUw");
					sendMessage(sender, "&7" + SCUtils.getMessageFromMessagesFile("Commands.Info.Donate") + ": &3https://goo.gl/sY8Gvi");
				}
			}
		});

	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return Arrays.asList("-dev");
	}
	
	private void sendMessage(CommandSender sender, String msg) {
		sender.sendMessage(SCUtils.color(msg));
	}
}