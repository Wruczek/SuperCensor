package mr.wruczek.supercensor3.commands.subcommands;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mr.wruczek.supercensor3.SCConfigManager2;
import mr.wruczek.supercensor3.SCInitManager;
import mr.wruczek.supercensor3.SCMain;
import mr.wruczek.supercensor3.commands.SCMainCommand;
import mr.wruczek.supercensor3.commands.SCSubcommand;
import mr.wruczek.supercensor3.utils.ConfigUtils;
import mr.wruczek.supercensor3.utils.IOUtils;
import mr.wruczek.supercensor3.utils.LoggerUtils;
import mr.wruczek.supercensor3.utils.SCUtils;
import mr.wruczek.supercensor3.utils.StringUtils;
import mr.wruczek.supercensor3.utils.classes.SCLogger;
import mr.wruczek.supercensor3.utils.classes.SCPermissionsEnum;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0
 * International License. http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SubcommandSetlang extends SCSubcommand {

    private static boolean playerGreeted;

    public SubcommandSetlang() {
        SCMainCommand.registerSubcommand(this, "setlang", "sl");
        SCMainCommand.registerTabCompletion(this);
    }

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {

        if (!SCUtils.checkPermissions(sender, SCPermissionsEnum.BASICADMIN.toString())) {
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(
                    SCUtils.getPluginPrefix() + StringUtils.color("&eWelcome to SuperCensor language configuration!\n"
                            + "&7Use command &6/sc sl [language code]&7 to change plugin language\n"
                            + "&7Please use language code (EN, DE, RU, PL) instead of language name"
                            + getAvailableLangsMessage()));
            return;
        }

        String newlang = args[1].toLowerCase();

        try {
            if (SCMain.getInstance().getResource("messages/messages_" + newlang + ".yml") == null) {
                sender.sendMessage(StringUtils
                        .color(SCUtils.getPluginPrefix() + "&cMessages file for that language was not found.\n"
                                + "Please use language code (EN, DE, RU, PL) instead of language name"
                                + getAvailableLangsMessage() + "\n"
                                + "&7Use command &6/sc setlang [language code]&7 to change plugin language"));
                return;
            }

            SCConfigManager2.config.set("Language", newlang);
            SCConfigManager2.config.save();

            SCInitManager.init();

            sender.sendMessage(
                    SCUtils.getPluginPrefix() + ChatColor.GOLD
                            + ConfigUtils.getMessageFromMessagesFile("SystemEnable.MessagesLoaded")
                                    .replace("%languagecode%",
                                            ConfigUtils.getMessageFromMessagesFile(
                                                    "LocalizationInformations.LanguageCode"))
                    .replace("%languagename%",
                            ConfigUtils.getMessageFromMessagesFile("LocalizationInformations.Language")));
        } catch (Exception e) {

            sender.sendMessage(SCUtils.getPluginPrefix() + ChatColor.RED
                    + "An exception occurred while attemping to change plugin language! "
                    + "Please check console for more informations");

            sender.sendMessage(ChatColor.RED + e.toString());

            SCLogger.logError("Exception while setting plugin language", LoggerUtils.LogType.PLUGIN);
            LoggerUtils.handleException(e);
        }
    }

    public static void greetPlayer(Player player) {
        if (!player.hasPermission(SCPermissionsEnum.BASICADMIN.toString()) || !SCConfigManager2.freshlyInstalled || playerGreeted) {
            return;
        }

        playerGreeted = true;

        String message = "\n&6SuperCensor has been successfully installed!\n"
                + "&6You can use command &e/sc setlang [language code]&6\n"
                + "&6to change plugin language." + getAvailableLangsMessage() + "\n ";

        message = StringUtils.color(message);

        SCLogger.logInfo(message);
        player.sendMessage(message);
    }

    public static String getAvailableLangsMessage() {
        String availableLangs = "";

        try {
            availableLangs = "\n&6Avaiable languages: &7" + getAvailableLangs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return availableLangs;
    }

    public static String getAvailableLangs() throws IOException, URISyntaxException {

        StringBuilder sb = new StringBuilder();

        for (String str : getAvailableLangsList()) {

            if(str.equalsIgnoreCase(SCConfigManager2.config.getString("Language")))
                str = StringUtils.color("&a" + str + " (CURRENT)&7");

            sb.append(str + ", ");
        }

        String result = sb.toString().trim();

        return result.substring(0, result.length() - 1);
    }

    public static List<String> getAvailableLangsList() throws IOException, URISyntaxException {

        List<String> result = new ArrayList<>();

        for(String str : IOUtils.getResourceListing(SCMain.class, "messages/")) {

            str = str.toLowerCase();

            if(str.endsWith(".yml"))
                result.add(str.replace("messages_", "").replace(".yml", "").toUpperCase());
        }

        Collections.sort(result);

        return result;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return args.length == 2 ? Arrays.asList("[language code]") : Arrays.asList("");
    }
}
