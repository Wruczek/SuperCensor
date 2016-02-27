package mr.wruczek.supercensor3.checks;

import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import mr.wruczek.supercensor3.SCCheckEvent;
import mr.wruczek.supercensor3.SCMain;
import mr.wruczek.supercensor3.data.SCPlayerDataManger;
import mr.wruczek.supercensor3.utils.SCLogger;
import mr.wruczek.supercensor3.utils.SCLogger.LogType;
import mr.wruczek.supercensor3.utils.SCUtils;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0
 * International License. http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SpecialCheck implements Listener {

	private Random random = new Random();
	int addedPenaltyPoints;
	String wordToCheck;
	
	@EventHandler
	public void checkListener(final SCCheckEvent event) {

		if (event.isCensored())
			return;

		for (String str : event.getMessage().split(" ")) {
			wordToCheck = str.toLowerCase();

			for (final ConfigurationSection specialLists : CensorData.special) {
				for (final String specialEntries : specialLists.getKeys(false)) {

					if (specialLists.contains(specialEntries + ".SimpleRegex")) {
						
						List<String> regexList = specialLists.getStringList(specialEntries + ".SimpleRegex");

						boolean found = false;

						for (String regex : regexList) {
							if (SCUtils.checkRegex(regex, wordToCheck)) {
								found = true;
								break;
							}
						}

						if (!found)
							continue; // This is horrible. I know.

						
					} else if (specialLists.contains(specialEntries + ".RegexIds")) {
						List<String> regexList = specialLists.getStringList(specialEntries + ".RegexIds");

						boolean found = false;

						for (String regexName : regexList) {
							String regex = null;

							for (Entry<String, String> regexFinder : CensorData.regexList.entrySet())
								if (regexFinder.getKey().equalsIgnoreCase(regexName))
									regex = regexFinder.getValue();

							if (regex != null && SCUtils.checkRegex(regex, wordToCheck)) {
								found = true;
								break;
							}
						}

						if (!found)
							continue; // This is horrible. I know.

					} else if (specialLists.contains(specialEntries + ".Normal")) {
						if (!specialLists.getString(specialEntries + ".Normal").equalsIgnoreCase(wordToCheck))
							continue;
					} else if (specialLists.contains(specialEntries + ".CheckFullMessage")) {
						wordToCheck = event.getMessage();
					}
					
					// Check for bypass permission
					if (event.getPlayer().hasPermission("supercensor.bypass.special." + specialEntries))
						continue;
					
					/* **************************** */
					/* CHECKS */
					/* **************************** */
					
					if (specialLists.contains(specialEntries + ".OnCapsPercent"))
						if (specialLists.getDouble(specialEntries + ".OnCapsPercent") > SCUtils
								.getCapsPercent(wordToCheck))
							continue;

					// wordToCheck is passing...

					// Minimum length
					if (specialLists.contains(specialEntries + ".MinLength"))
						if (wordToCheck.length() < specialLists.getInt(specialEntries + ".MinLength"))
							continue;
					
					// Maximum length
					if (specialLists.contains(specialEntries + ".MaxLength"))
						if (wordToCheck.length() > specialLists.getInt(specialEntries + ".MaxLength"))
							continue;

					/* **************************** */
					/* RUNNING ACTIONS */
					/* **************************** */

					// Cancel event
					if (specialLists.contains(specialEntries + ".CancelChatEvent")
							&& specialLists.getBoolean(specialEntries + ".CancelChatEvent"))
						event.setCensored(true);

					addedPenaltyPoints = 0;

					// Add PenaltyPoints
					if (specialLists.contains(specialEntries + ".PenaltyPoints")) {
						addedPenaltyPoints = specialLists.getInt(specialEntries + ".PenaltyPoints");
						new SCPlayerDataManger(event.getPlayer()).addPenalityPoints(addedPenaltyPoints);
					}

					// Message player
					if (specialLists.contains(specialEntries + ".MessagePlayer"))
						event.getPlayer()
								.sendMessage(SCUtils
										.color(specialLists.getString(specialEntries + ".MessagePlayer")
												.replace("%nick%", event.getPlayer().getDisplayName()))
										.replace("%addedpenaltypoints%", String.valueOf(addedPenaltyPoints))
										.replace("%censoredword%", wordToCheck));

					// Run commands
					if (specialLists.contains(specialEntries + ".RunCommands")) {
						// We want to sync it with Bukkit thread to avoid
						// java.lang.IllegalStateException and allow things like
						// kicking players
						Bukkit.getScheduler().scheduleSyncDelayedTask(SCMain.getInstance(), new Runnable() {
							@Override
							public void run() {
								for (String command : specialLists.getStringList(specialEntries + ".RunCommands")) {
									try {
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
												command.replace("%nick%", event.getPlayer().getName())
														.replace("%addedpenaltypoints%",
																String.valueOf(addedPenaltyPoints))
												.replace("%censoredword%", wordToCheck));
									} catch (Exception e) {
										SCUtils.logError(
												"There was exception when executing command \"" + command
														+ "\" on player \"" + event.getPlayer().getName(),
												LogType.PLUGIN);
									}
								}
							}
						});
					}

					if (specialLists.contains(specialEntries + ".CancelChatEvent")
							&& specialLists.getBoolean(specialEntries + ".CancelChatEvent"))
						event.setCensored(true);

					// Replace to lowercase
					if (specialLists.contains(specialEntries + ".ReplaceToLowercase")
							&& specialLists.getBoolean(specialEntries + ".ReplaceToLowercase")) {
						event.setMessage(event.getMessage().toLowerCase());
					}

					// Replace
					if (specialLists.contains(specialEntries + ".ReplaceTo") && !event.isCensored()) {
						List<String> replaceToList = specialLists.getStringList(specialEntries + ".ReplaceTo");

						String replaceTo = replaceToList.get(random.nextInt(replaceToList.size()));

						event.setMessage(SCUtils.replaceIgnoreCase(event.getMessage().toLowerCase(),
								wordToCheck.toLowerCase(), replaceTo));
					}

					// Log
					if (specialLists.contains(specialEntries + ".Log")) {
						SCUtils.logInfo(specialLists.getString(specialEntries + ".Log")
								.replace("%date%", SCLogger.getDate()).replace("%time%", SCLogger.getTime())
								.replace("%nick%", event.getPlayer().getName()).replace("%swearword%", wordToCheck)
								.replace("%message%", event.getOriginalMessage()), LogType.CENSOR);
					}
				}
			}
		}
	}

}
