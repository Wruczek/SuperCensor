package mr.wruczek.supercensor3.checks;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import mr.wruczek.supercensor3.SCCheckEvent;
import mr.wruczek.supercensor3.SCConfigManager2;
import mr.wruczek.supercensor3.SCMain;
import mr.wruczek.supercensor3.PPUtils.PPManager;
import mr.wruczek.supercensor3.utils.SCLogger;
import mr.wruczek.supercensor3.utils.SCLogger.LogType;
import mr.wruczek.supercensor3.utils.SCPermissionsEnum;
import mr.wruczek.supercensor3.utils.SCUtils;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class WordlistCheck implements Listener {

	private Random random = new Random();
	
	@EventHandler
	public void checkListener(final SCCheckEvent event) {
		
		if (event.isCensored() || SCPermissionsEnum.WORDLIST_BYPASS.hasPermission(event.getPlayer()))
			return;
		
		String messageToCheck = event.getOriginalMessage();
		
		// Replace @ to a, $ to s and remove spaces to avoid exploits
		if (SCConfigManager2.config.getBoolean("WordlistSettings.DeepSearch")) {
			messageToCheck = messageToCheck.replace(" ", "");
			messageToCheck = messageToCheck.replace("@", "a");
			messageToCheck = messageToCheck.replace("$", "s");
		}
		
		mainLoop: for(String message : messageToCheck.split(" ")) {
			
			// Replace special charters
			for(char specialChar : SCConfigManager2.config.getString("WordlistSettings.SpecialCharters").toCharArray()) {
				message = message.replace(String.valueOf(specialChar), "");
			}
			
			// Check for whitelist
			for(String whitelist : CensorData.whitelist) {
				if(message.toLowerCase().contains(whitelist.toLowerCase())) {
					continue mainLoop;
				}
			}
			
			for(final String censoredWord : CensorData.wordlist) {
				
				/*
				System.out.println("--> " + censoredWord);
				
				System.out.println(message.toLowerCase().contains(censoredWord.toLowerCase()));
				System.out.println(message.matches(censoredWord));
				
				System.out.println(!(message.toLowerCase().contains(censoredWord.toLowerCase()) && message.matches(censoredWord)));
				System.out.println(!(message.toLowerCase().contains(censoredWord.toLowerCase()) || message.matches(censoredWord)));
				*/
				
				String lowerCaseMessage = message.toLowerCase();
				
				if (!(lowerCaseMessage.contains(censoredWord.toLowerCase()) || SCUtils.checkRegex(censoredWord, lowerCaseMessage))) {
					continue;
				}
				
				// Cancel event
				if (SCConfigManager2.config.getBoolean("WordlistSettings.CancelMessage"))
					event.setCensored(true);
				
				// Send message to player
				String mtp = SCConfigManager2.messages.getString("WordlistSettings.MessageToPlayer");
				if (mtp != null)
					event.getPlayer().sendMessage(SCUtils.color(mtp));
				
				// Replace all swear words
				if (SCConfigManager2.config.getBoolean("WordlistSettings.Replace.Enabled") && !event.isCensored()) {
					
					List<String> replaceToList = SCConfigManager2.config.getStringList("WordlistSettings.Replace.ReplaceTo");
					String replaceTo = replaceToList.get(random.nextInt(replaceToList.size()));
					
					String newMessage = event.getMessage();
					
					for(final String target : CensorData.wordlist) {
						replaceTo = replaceToList.get(random.nextInt(replaceToList.size()));
						newMessage = SCUtils.replaceIgnoreCase(newMessage, target, replaceTo);
					}
					
					event.setMessage(newMessage);
				}
				
				// Add PenaltyPoints
				if (SCConfigManager2.config.contains("WordlistSettings.PenaltyPoints")) {
					int points = SCConfigManager2.config.getInt("WordlistSettings.PenaltyPoints");
					PPManager.addPenaltyPoints(event.getPlayer(), points, true);
				}
				
				// Run commands
				if(SCConfigManager2.config.getBoolean("WordlistSettings.RunCommands.Enabled")) {
					for(final String command : SCConfigManager2.config.getStringList("WordlistSettings.RunCommands.Commands")) {
						// We want to sync it with Bukkit thread to avoid 
						//	java.lang.IllegalStateException and allow things like kicking players
						Bukkit.getScheduler().scheduleSyncDelayedTask(SCMain.getInstance(), new Runnable() {
							public void run() {
								try {
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
											.replace("%nick%", event.getPlayer().getName())
											.replace("%swearword%", censoredWord));
								} catch (Exception e) {
									SCUtils.logError("There was exception when executing command \"" + command
											+ "\" on player \"" + event.getPlayer().getName(), LogType.PLUGIN);
								}
							}
						});
					}
				}
				
				// Log
				if(SCConfigManager2.config.getBoolean("WordlistSettings.Log.Enabled")) {
					SCUtils.logInfo(SCConfigManager2.config.getString("WordlistSettings.Log.Format")
										.replace("%date%", SCLogger.getDate())
										.replace("%time%", SCLogger.getTime())
										.replace("%nick%", event.getPlayer().getName())
										.replace("%swearword%", censoredWord)
										.replace("%message%", event.getOriginalMessage()), LogType.CENSOR);
				}
				
				return; // Cancel the loops after taking action
			}
		}
	}
}