package mr.wruczek.supercensor3.checks;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import mr.wruczek.supercensor3.PPUtils.PPManager;
import mr.wruczek.supercensor3.SCCheckEvent;
import mr.wruczek.supercensor3.SCConfigManager2;
import mr.wruczek.supercensor3.SCMain;
<<<<<<< HEAD
=======
import mr.wruczek.supercensor3.PPUtils.PPManager;
import mr.wruczek.supercensor3.commands.subcommands.SubcommandInfo;
>>>>>>> origin/master
import mr.wruczek.supercensor3.utils.ConfigUtils;
import mr.wruczek.supercensor3.utils.LoggerUtils;
import mr.wruczek.supercensor3.utils.StringUtils;
import mr.wruczek.supercensor3.utils.classes.SCLogger;
import mr.wruczek.supercensor3.utils.classes.SCPermissionsEnum;

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
        if (ConfigUtils.getBooleanFromConfig("WordlistSettings.DeepSearch")) {
            messageToCheck = messageToCheck.replace(" ", "");
            messageToCheck = messageToCheck.replace("@", "a");
            messageToCheck = messageToCheck.replace("$", "s");
        }

        mainLoop:
        for (String message : messageToCheck.split(" ")) {

            // Replace special charters
            for (char specialChar : ConfigUtils.getStringFromConfig("WordlistSettings.SpecialCharters").toCharArray()) {
                message = message.replace(String.valueOf(specialChar), "");
            }

            // Check for whitelist
            for (String whitelist : CensorData.whitelist) {
                if (message.toLowerCase().contains(whitelist.toLowerCase())) {
                    continue mainLoop;
                }
            }

            for (final String censoredWord : CensorData.wordlist) {

				/*
				System.out.println("--> " + censoredWord);
				
				System.out.println(message.toLowerCase().contains(censoredWord.toLowerCase()));
				System.out.println(message.matches(censoredWord));
				
				System.out.println(!(message.toLowerCase().contains(censoredWord.toLowerCase()) && message.matches(censoredWord)));
				System.out.println(!(message.toLowerCase().contains(censoredWord.toLowerCase()) || message.matches(censoredWord)));
				*/
<<<<<<< HEAD

                String lowerCaseMessage = message.toLowerCase();

                if (!(lowerCaseMessage.contains(censoredWord.toLowerCase()) || StringUtils.checkRegex(censoredWord, lowerCaseMessage))) {
                    continue;
                }

                // Cancel event
                if (ConfigUtils.getBooleanFromConfig("WordlistSettings.CancelMessage"))
                    event.setCensored(true);

                // Send message to player
                String mtp = SCConfigManager2.messages.getString("WordlistSettings.MessageToPlayer");
                if (mtp != null)
                    event.getPlayer().sendMessage(StringUtils.color(mtp));

                // Replace all swear words
                if (ConfigUtils.getBooleanFromConfig("WordlistSettings.Replace.Enabled") && !event.isCensored()) {

                    List<String> replaceToList = ConfigUtils.getStringListFromConfig("WordlistSettings.Replace.ReplaceTo");
                    String replaceTo = replaceToList.get(random.nextInt(replaceToList.size()));

                    String newMessage = event.getMessage();

                    for (final String target : CensorData.wordlist) {
                        replaceTo = replaceToList.get(random.nextInt(replaceToList.size()));
                        newMessage = StringUtils.replaceIgnoreCase(newMessage, target, replaceTo);
                    }

                    event.setMessage(newMessage);
                }

                // Add PenaltyPoints
                if (ConfigUtils.configContains("WordlistSettings.PenaltyPoints")) {
                    int points = ConfigUtils.getIntFromConfig("WordlistSettings.PenaltyPoints");
                    PPManager.addPenaltyPoints(event.getPlayer(), points, true);
                }

                // Run commands
                if (ConfigUtils.getBooleanFromConfig("WordlistSettings.RunCommands.Enabled")) {
                    for (final String command : ConfigUtils.getStringListFromConfig("WordlistSettings.RunCommands.Commands")) {
                        // We want to sync it with Bukkit thread to avoid
                        //	java.lang.IllegalStateException and allow things like kicking players
                        Bukkit.getScheduler().scheduleSyncDelayedTask(SCMain.getInstance(), new Runnable() {
                            public void run() {
                                try {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                                            .replace("%nick%", event.getPlayer().getName())
                                            .replace("%swearword%", censoredWord));
                                } catch (Exception e) {
                                    SCLogger.logError("There was exception when executing command \"" + command
                                            + "\" on player \"" + event.getPlayer().getName(), LoggerUtils.LogType.PLUGIN);
                                }
                            }
                        });
                    }
                }

                // Log
                if (ConfigUtils.getBooleanFromConfig("WordlistSettings.Log.Enabled")) {
                    SCLogger.logInfo(ConfigUtils.getStringFromConfig("WordlistSettings.Log.Format")
                            .replace("%date%", LoggerUtils.getDate())
                            .replace("%time%", LoggerUtils.getTime())
                            .replace("%nick%", event.getPlayer().getName())
                            .replace("%swearword%", censoredWord)
                            .replace("%message%", event.getOriginalMessage()), LoggerUtils.LogType.CENSOR);
                }

                return; // Cancel the loops after taking action
            }
        }
    }
=======
				
				String lowerCaseMessage = message.toLowerCase();
				
				if (!(lowerCaseMessage.contains(censoredWord.toLowerCase()) || StringUtils.checkRegex(censoredWord, lowerCaseMessage, true))) {
					continue;
				}
				
				SubcommandInfo.latestFilter = "W:" + censoredWord;
				
				// Cancel event
				if (ConfigUtils.getBooleanFromConfig("WordlistSettings.CancelMessage"))
					event.setCensored(true);
				
				// Send message to player
				String mtp = SCConfigManager2.messages.getString("WordlistSettings.MessageToPlayer");
				if (mtp != null)
					event.getPlayer().sendMessage(StringUtils.color(mtp));
				
				// Replace all swear words
				if (ConfigUtils.getBooleanFromConfig("WordlistSettings.Replace.Enabled") && !event.isCensored()) {
					
					List<String> replaceToList = ConfigUtils.getStringListFromConfig("WordlistSettings.Replace.ReplaceTo");
					String replaceTo = replaceToList.get(random.nextInt(replaceToList.size()));
					
					String newMessage = event.getMessage();
					
					for(final String target : CensorData.wordlist) {
						replaceTo = replaceToList.get(random.nextInt(replaceToList.size()));
						newMessage = StringUtils.replaceIgnoreCase(newMessage, target, replaceTo);
					}
					
					event.setMessage(newMessage);
				}
				
				// Add PenaltyPoints
				if (ConfigUtils.configContains("WordlistSettings.PenaltyPoints")) {
					int points = ConfigUtils.getIntFromConfig("WordlistSettings.PenaltyPoints");
					PPManager.addPenaltyPoints(event.getPlayer(), points, true);
				}
				
				// Run commands
				if(ConfigUtils.getBooleanFromConfig("WordlistSettings.RunCommands.Enabled")) {
					for(final String command : ConfigUtils.getStringListFromConfig("WordlistSettings.RunCommands.Commands")) {
						// We want to sync it with Bukkit thread to avoid 
						//	java.lang.IllegalStateException and allow things like kicking players
						Bukkit.getScheduler().scheduleSyncDelayedTask(SCMain.getInstance(), new Runnable() {
							public void run() {
								try {
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
											.replace("%nick%", event.getPlayer().getName())
											.replace("%swearword%", censoredWord));
								} catch (Exception e) {
									SCLogger.logError("There was exception when executing command \"" + command
											+ "\" on player \"" + event.getPlayer().getName(), LoggerUtils.LogType.PLUGIN);
								}
							}
						});
					}
				}
				
				// Log
				if(ConfigUtils.getBooleanFromConfig("WordlistSettings.Log.Enabled")) {
					SCLogger.logInfo(ConfigUtils.getStringFromConfig("WordlistSettings.Log.Format")
										.replace("%date%", LoggerUtils.getDate())
										.replace("%time%", LoggerUtils.getTime())
										.replace("%nick%", event.getPlayer().getName())
										.replace("%swearword%", censoredWord)
										.replace("%message%", event.getOriginalMessage()), LoggerUtils.LogType.CENSOR);
				}
				
				return; // Cancel the loops after taking action
			}
		}
	}
>>>>>>> origin/master
}