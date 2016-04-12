package mr.wruczek.supercensor3.checks;

import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import mr.wruczek.supercensor3.PPUtils.PPManager;
import mr.wruczek.supercensor3.SCCheckEvent;
import mr.wruczek.supercensor3.SCMain;
<<<<<<< HEAD
=======
import mr.wruczek.supercensor3.PPUtils.PPManager;
import mr.wruczek.supercensor3.commands.subcommands.SubcommandInfo;
>>>>>>> origin/master
import mr.wruczek.supercensor3.utils.LoggerUtils;
import mr.wruczek.supercensor3.utils.StringUtils;
import mr.wruczek.supercensor3.utils.classes.SCLogger;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0
 * International License. http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SpecialCheck implements Listener {

<<<<<<< HEAD
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
                            if (StringUtils.checkRegex(regex, wordToCheck)) {
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

                            if (regex != null && StringUtils.checkRegex(regex, wordToCheck)) {
                                found = true;
                                break;
                            }
                        }

                        if (!found)
                            continue;

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

                    // region Caps percent check
                    if (specialLists.contains(specialEntries + ".OnCapsPercent"))
                        if (specialLists.getDouble(specialEntries + ".OnCapsPercent") > StringUtils.getCapsPercent(wordToCheck))
                            continue;
                    // endregion

                    // wordToCheck is passing...

                    // region Minimum length check
                    if (specialLists.contains(specialEntries + ".MinLength"))
                        if (wordToCheck.length() < specialLists.getInt(specialEntries + ".MinLength"))
                            continue;
                    // endregion

                    // region Maximum length
                    if (specialLists.contains(specialEntries + ".MaxLength"))
                        if (wordToCheck.length() > specialLists.getInt(specialEntries + ".MaxLength"))
                            continue;
                    // endregion
=======
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
							if (StringUtils.checkRegex(regex, wordToCheck, true)) {
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

							if (regex != null && StringUtils.checkRegex(regex, wordToCheck, true)) {
								found = true;
								break;
							}
						}

						if (!found)
							continue;

					} else if (specialLists.contains(specialEntries + ".Normal")) {
						if (!specialLists.getString(specialEntries + ".Normal").equalsIgnoreCase(wordToCheck))
							continue;
					} else if (specialLists.contains(specialEntries + ".CheckFullMessage")) {
						wordToCheck = event.getMessage();
					}
					
					// Check for bypass permission
					if (event.getPlayer().hasPermission("supercensor.bypass.special." + specialEntries))
						continue;
					
					SubcommandInfo.latestFilter = "S:" + specialEntries;
					
					/* **************************** */
					/* CHECKS */
					/* **************************** */
					
					// region Caps percent check
					if (specialLists.contains(specialEntries + ".OnCapsPercent"))
						if (specialLists.getDouble(specialEntries + ".OnCapsPercent") > StringUtils.getCapsPercent(str))
							continue;
					// endregion

					// wordToCheck is passing...

					// region Minimum length check
					if (specialLists.contains(specialEntries + ".MinLength"))
						if (wordToCheck.length() < specialLists.getInt(specialEntries + ".MinLength"))
							continue;
					// endregion
					
					// region Maximum length
					if (specialLists.contains(specialEntries + ".MaxLength"))
						if (wordToCheck.length() > specialLists.getInt(specialEntries + ".MaxLength"))
							continue;
					// endregion
>>>>>>> origin/master

					/* **************************** */
					/* RUNNING ACTIONS */
					/* **************************** */

                    // region Action Cancel event
                    if (specialLists.contains(specialEntries + ".CancelChatEvent")
                            && specialLists.getBoolean(specialEntries + ".CancelChatEvent"))
                        event.setCensored(true);
                    // endregion

                    addedPenaltyPoints = 0;

                    // region Action Add PenaltyPoints
                    if (specialLists.contains(specialEntries + ".PenaltyPoints")) {
                        addedPenaltyPoints = specialLists.getInt(specialEntries + ".PenaltyPoints");
                        PPManager.addPenaltyPoints(event.getPlayer(), addedPenaltyPoints, true);
                    }
                    // endregion

                    // region Action Message player
                    if (specialLists.contains(specialEntries + ".MessagePlayer"))
                        event.getPlayer().sendMessage(StringUtils
                                .color(specialLists.getString(specialEntries + ".MessagePlayer")
                                        .replace("%nick%", event.getPlayer().getDisplayName()))
                                .replace("%addedpenaltypoints%", String.valueOf(addedPenaltyPoints))
                                .replace("%censoredword%", wordToCheck));
                    // endregion

                    // region Action Run commands
                    if (specialLists.contains(specialEntries + ".RunCommands")) {
                        // We want to sync it with Bukkit thread to avoid
                        // java.lang.IllegalStateException and allow things like
                        // kicking players
                        Bukkit.getScheduler().scheduleSyncDelayedTask(SCMain.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                for (String command : specialLists.getStringList(specialEntries + ".RunCommands")) {
                                    try {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                                                .replace("%nick%", event.getPlayer().getName())
                                                .replace("%addedpenaltypoints%", String.valueOf(addedPenaltyPoints))
                                                .replace("%censoredword%", wordToCheck));
                                    } catch (Exception e) {
                                        SCLogger.logError("There was exception when executing command \"" + command
                                                + "\" on player \"" + event.getPlayer().getName(), LoggerUtils.LogType.PLUGIN);
                                    }
                                }
                            }
                        });
                    }
                    // endregion

                    // region Action Replace to lowercase
                    if (specialLists.contains(specialEntries + ".ReplaceToLowercase")
                            && specialLists.getBoolean(specialEntries + ".ReplaceToLowercase")) {
                        event.setMessage(event.getMessage().toLowerCase());
                    }
                    // endregion

                    // region Action Replace
                    if (specialLists.contains(specialEntries + ".ReplaceTo") && !event.isCensored()) {
                        List<String> replaceToList = specialLists.getStringList(specialEntries + ".ReplaceTo");

                        String replaceTo = replaceToList.get(random.nextInt(replaceToList.size()));

                        event.setMessage(StringUtils.replaceIgnoreCase(event.getMessage().toLowerCase(),
                                wordToCheck.toLowerCase(), replaceTo));
                    }
                    // endregion

                    // region Action Log
                    if (specialLists.contains(specialEntries + ".Log")) {
                        SCLogger.logInfo(specialLists.getString(specialEntries + ".Log")
                                .replace("%date%", LoggerUtils.getDate()).replace("%time%", LoggerUtils.getTime())
                                .replace("%nick%", event.getPlayer().getName()).replace("%swearword%", wordToCheck)
                                .replace("%message%", event.getOriginalMessage()), LoggerUtils.LogType.CENSOR);
                    }
                    // endregion
                }
            }
        }
    }

}
