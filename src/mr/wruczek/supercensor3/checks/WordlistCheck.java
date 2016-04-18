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
import mr.wruczek.supercensor3.commands.subcommands.SubcommandInfo;
import mr.wruczek.supercensor3.utils.ConfigUtils;
import mr.wruczek.supercensor3.utils.LoggerUtils;
import mr.wruczek.supercensor3.utils.StringUtils;
import mr.wruczek.supercensor3.utils.classes.SCLogger;
import mr.wruczek.supercensor3.utils.classes.SCPermissionsEnum;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0
 * International License. http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class WordlistCheck implements Listener {

    private Random random = new Random();
    String matcherResult;

    @EventHandler
    public void checkListener(final SCCheckEvent event) {

        if (event.isCensored() || (SCPermissionsEnum.WORDLIST_BYPASS.hasPermission(event.getPlayer())
                && !SCConfigManager2.config.getBoolean("General.AdminCensor")))
            return;

        String messageToCheck = event.getMessage();

        // Replace @ to a, $ to s and remove spaces to avoid exploits
        if (ConfigUtils.getBooleanFromConfig("WordlistSettings.DeepSearch")) {
            messageToCheck = messageToCheck.replace(" ", "");
            messageToCheck = messageToCheck.replace("@", "a");
            messageToCheck = messageToCheck.replace("$", "s");
        }

        mainLoop: for (String message : messageToCheck.split(" ")) {

            // Replace special charters
            for (char specialChar : ConfigUtils.getStringFromConfig("WordlistSettings.SpecialCharters").toCharArray()) {
                message = message.replace(String.valueOf(specialChar), "");
            }

            for (final String checkAgainst : CensorData.wordlist) {

                /*
                System.out.println("--> " + censoredWord);

                System.out.println(message.toLowerCase().contains(censoredWord.toLowerCase()));
                System.out.println(message.matches(censoredWord));

                System.out.println(!(message.toLowerCase().contains(censoredWord.toLowerCase()) && message.matches(censoredWord)));
                System.out.println(!(message.toLowerCase().contains(censoredWord.toLowerCase()) || message.matches(censoredWord)));
                */

                String lowerCaseMessage = message.toLowerCase();

                matcherResult = StringUtils.checkRegex(checkAgainst, lowerCaseMessage, true);

                if (!lowerCaseMessage.contains(checkAgainst.toLowerCase()) && matcherResult == null) {
                    continue;
                }

                if (matcherResult == null)
                    matcherResult = checkAgainst.toLowerCase();

                // Check for whitelist - TODO
                for (String whitelist : CensorData.whitelist) {
                    if (matcherResult.toLowerCase().contains(whitelist.toLowerCase())) {
                        continue mainLoop;
                    }
                }

                SubcommandInfo.latestFilter = "W:" + checkAgainst;

                // region Cancel event
                if (ConfigUtils.getBooleanFromConfig("WordlistSettings.CancelMessage"))
                    event.setCensored(true);
                // endregion

                // region Send message to player
                String mtp = SCConfigManager2.messages.getString("WordlistSettings.MessageToPlayer");
                if (mtp != null)
                    event.getPlayer().sendMessage(StringUtils.color(mtp));
                // endregion

                // region Add PenaltyPoints
                if (ConfigUtils.configContains("WordlistSettings.PenaltyPoints")) {

                    if (SCPermissionsEnum.WORDLIST_BYPASS.hasPermission(event.getPlayer()))
                        return;

                    int points = ConfigUtils.getIntFromConfig("WordlistSettings.PenaltyPoints");
                    PPManager.addPenaltyPoints(event.getPlayer(), points, true);
                }
                // endregion

                // region Run commands
                if (ConfigUtils.getBooleanFromConfig("WordlistSettings.RunCommands.Enabled")) {
                    for (final String command : ConfigUtils
                            .getStringListFromConfig("WordlistSettings.RunCommands.Commands")) {
                        // We want to sync it with Bukkit thread to avoid
                        // java.lang.IllegalStateException and allow things like
                        // kicking players
                        Bukkit.getScheduler().scheduleSyncDelayedTask(SCMain.getInstance(), new Runnable() {
                            public void run() {
                                try {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                            command.replace("%nick%", event.getPlayer().getName())
                                                    .replace("%swearword%", matcherResult));
                                } catch (Exception e) {
                                    SCLogger.logError(
                                            "There was exception when executing command \"" + command
                                                    + "\" on player \"" + event.getPlayer().getName(),
                                            LoggerUtils.LogType.PLUGIN);
                                }
                            }
                        });
                    }
                }
                // endregion

                // region Log
                if (ConfigUtils.getBooleanFromConfig("WordlistSettings.Log.Enabled")) {
                    SCLogger.logInfo(ConfigUtils.getStringFromConfig("WordlistSettings.Log.Format")
                            .replace("%date%", LoggerUtils.getDate()).replace("%time%", LoggerUtils.getTime())
                            .replace("%nick%", event.getPlayer().getName()).replace("%swearword%", matcherResult)
                            .replace("%message%", event.getOriginalMessage()), LoggerUtils.LogType.CENSOR);
                }
                // endregion

                // region Replace all swear words
                if (ConfigUtils.getBooleanFromConfig("WordlistSettings.Replace.Enabled")) {

                    // REPLACING
                    List<String> replaceToList = ConfigUtils
                            .getStringListFromConfig("WordlistSettings.Replace.ReplaceTo");
                    String replaceTo = replaceToList.get(random.nextInt(replaceToList.size()));
                    // String newMessage =
                    // StringUtils.replaceIgnoreCase(event.getMessage(),
                    // matcherResult, replaceTo);
                    // String newMessage =
                    // event.getMessage().replaceAll(lowerCaseMessage,
                    // replaceTo);

                    // System.out.println(lowerCaseMessage);

                    String newMessage = StringUtils.replaceIgnoreCase(event.getMessage(), lowerCaseMessage, replaceTo);

                    event.setMessage(newMessage);

                    if (event.isCensored())
                        event.setCensored(false);
                }
                // endregion

                return; // Cancel the loops after taking action
            }
        }
    }
}
