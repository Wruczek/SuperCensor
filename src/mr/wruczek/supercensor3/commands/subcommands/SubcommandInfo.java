package mr.wruczek.supercensor3.commands.subcommands;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import mr.wruczek.supercensor3.utils.ConfigUtils;
import mr.wruczek.supercensor3.utils.LoggerUtils;
import mr.wruczek.supercensor3.utils.SCUtils;
import mr.wruczek.supercensor3.utils.StringUtils;
import mr.wruczek.supercensor3.utils.TellrawUtils;
import mr.wruczek.supercensor3.utils.classes.GravityUpdater.UpdateResult;
import mr.wruczek.supercensor3.utils.classes.MessagesCreator;
import mr.wruczek.supercensor3.utils.classes.MessagesCreator.ChatExtra;
import mr.wruczek.supercensor3.utils.classes.Reflection;
import mr.wruczek.supercensor3.utils.classes.SCPermissionsEnum;
import mr.wruczek.supercensor3.utils.classes.SCUpdater;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SubcommandInfo extends SCSubcommand {

    private static String pluginInfoJSON;
    private static Map<String, String> links;
    public static String latestFilter;

    public SubcommandInfo() {
        SCMainCommand.registerSubcommand(this, "info", "informations", "about", "author", "version");
        SCMainCommand.registerTabCompletion(this);

        links = new LinkedHashMap<>();

        // TITLE, URL
        // links.put(ConfigUtils.getMessageFromMessagesFile("Commands.Info.BungeeCord"), "Comming soon!");
        links.put(ConfigUtils.getMessageFromMessagesFile("Commands.Info.BukkitDev"),        "https://goo.gl/HCXb1p");
        links.put(ConfigUtils.getMessageFromMessagesFile("Commands.Info.SpigotMC"),         "https://goo.gl/nUL9ur");
        links.put(ConfigUtils.getMessageFromMessagesFile("Commands.Info.Wiki"),             "https://goo.gl/kAUyu8");
        links.put(ConfigUtils.getMessageFromMessagesFile("Commands.Info.BugReporting"),     "https://goo.gl/0H6qfY");
        links.put(ConfigUtils.getMessageFromMessagesFile("Commands.Info.GitHub"),           "https://goo.gl/xvMzsJ");
        links.put(ConfigUtils.getMessageFromMessagesFile("Commands.Info.Collaborate"),      "https://goo.gl/qAFW2A");

        if (TellrawUtils.isTellrawSupportedByServer())
            pluginInfoJSON = generateJSONString(links);
    }

    @Override
    public void onCommand(final CommandSender sender, String command, String[] args) {

        if (!SCUtils.checkPermissions(sender, SCPermissionsEnum.INFO.toString())) {
            return;
        }

        if (args.length > 1) {
            sender.sendMessage("SuperCensor "
                    + SCMain.getInstance().getDescription().getVersion()
                    + " - Developer Informations");

            sender.sendMessage("Server version: " + Bukkit.getVersion());

            sender.sendMessage(" ");

            if (SCSlowModeManager.getManager.getMap().isEmpty())
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
                for (String key : cs.getKeys(false))
                    sender.sendMessage("* CensorData.special: " + key);

            sender.sendMessage(" ");

            for (String str : CensorData.whitelist)
                sender.sendMessage("* CensorData.whitelist: " + str);

            sender.sendMessage(" ");

            for (AntiSpamData data : AntiSpam.getData())
                sender.sendMessage(data.toString());

            sender.sendMessage(" ");

            sender.sendMessage("Latest filter: " + latestFilter);

            sender.sendMessage(" ");

            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(SCMain.getInstance(), new Runnable() {
            @Override
            public void run() {
                PluginDescriptionFile pdf = SCMain.getInstance().getDescription();

                String addToVersion = "";

                if (SCUpdater.instance.isUpdaterEnabled()) {

                    sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.Info.CheckingForUpdates"));

                    UpdateResult result = SCUpdater.instance.checkForUpdates();

                    if (result == UpdateResult.NO_UPDATE) {
                        addToVersion = ConfigUtils.getMessageFromMessagesFile("Commands.Info.VersionStatus.UpToDate");
                    } else if (result == UpdateResult.UPDATE_AVAILABLE) {
                        addToVersion = ConfigUtils.getMessageFromMessagesFile("Commands.Info.VersionStatus.UpdateAvailable");
                    } else if (result == UpdateResult.SUCCESS) {
                        addToVersion = ConfigUtils.getMessageFromMessagesFile("Commands.Info.VersionStatus.NewVersionReady");
                    }
                }

                sender.sendMessage(SCCommandHeader.getHeader());
                sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.Info.Version") + pdf.getVersion() + addToVersion);
                sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.Info.Author") + "Wruczek");

                sender.sendMessage("");
                sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.Info.UsefulLinks"));

                if (TellrawUtils.isTellrawSupported(sender) && pluginInfoJSON != null && !pluginInfoJSON.isEmpty()) {
                    try {
                        Reflection.sendMessage((Player) sender, pluginInfoJSON);
                    } catch (Exception e) {
                        sender.sendMessage(StringUtils.color(SCUtils.getPluginPrefix() + "Cannot send you formatted message. Please check console for full stacktrace. " + e));
                        LoggerUtils.handleException(e);
                    }
                } else {
                    // For console / older Minecraft versions

                    for (Entry<String, String> link : links.entrySet()) {
                        sender.sendMessage(StringUtils.color("&7" + link.getKey() + ": &3" + link.getValue()));
                    }

                }
            }
        });

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Arrays.asList("-dev");
    }

    private static String generateJSONString(Map<String, String> links) {

        StringBuilder sb = new StringBuilder("[");

        boolean insertSeperator = false;

        for (Entry<String, String> entry : links.entrySet()) {

            MessagesCreator ms = new MessagesCreator("", null, null);
            ChatExtra extra = new MessagesCreator.ChatExtra(entry.getKey(), MessagesCreator.Color.GOLD, null);
            String hovertext = entry.getValue();

            if (entry.getValue().startsWith("http")) {
                extra.setClickEvent(MessagesCreator.ClickEventType.OPEN_URL, entry.getValue());
                hovertext = "Click to visit the website";
            }

            extra.setHoverEvent(MessagesCreator.HoverEventType.SHOW_TEXT, hovertext);

            ms.addExtra(extra);

            if (insertSeperator) {
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