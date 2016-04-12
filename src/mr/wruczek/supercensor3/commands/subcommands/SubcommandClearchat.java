package mr.wruczek.supercensor3.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mr.wruczek.supercensor3.commands.SCMainCommand;
import mr.wruczek.supercensor3.commands.SCSubcommand;
import mr.wruczek.supercensor3.utils.ConfigUtils;
import mr.wruczek.supercensor3.utils.SCUtils;
import mr.wruczek.supercensor3.utils.StringUtils;
import mr.wruczek.supercensor3.utils.classes.SCPermissionsEnum;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SubcommandClearchat extends SCSubcommand {

    public SubcommandClearchat() {
        SCMainCommand.registerSubcommand(this, "clear", "clearchat", "cc");
        SCMainCommand.registerTabCompletion(this);
    }

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {

        if (args.length < 2) {
            sender.sendMessage(SCUtils.getCommandDescription("Commands.ClearChat.CommandDescription"));
            sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.Usage") + " "
                    + StringUtils.usageFormatter("clear &8(&7clearchat&8)", "own&8|&6all&8|&8<&6playername", "!-a"));
            return;
        }

        CommandSender cc = sender;

        if (args.length > 2) {

            if (!SCPermissionsEnum.CLEARCHAT_ANONYMOUS.hasPermission(sender)) {
                sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.ClearChat.AnonymousModeNoPermissions"));
                return;
            }

            cc = null;

            sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.ClearChat.AnonymousMode"));
        }

        if (args[1].equalsIgnoreCase("own")) {

            if (!SCUtils.checkPermissions(sender, SCPermissionsEnum.CLEARCHAT_CLEAROWN.toString())) {
                return;
            }

            if (!(sender instanceof Player)) {
                for (int i = 0; i < ConfigUtils.getIntFromConfig("ClearChat.Lines.Console"); i++) {
                    System.out.println(" ");
                }
                return;
            }

            clearChat((Player) sender, sender, false, true);
        } else if (args[1].equalsIgnoreCase("all")) {

            if (!SCUtils.checkPermissions(sender, SCPermissionsEnum.CLEARCHAT_CLEARALL.toString())) {
                return;
            }

            int cleared = 0;
            int skipped = 1; // Set to one, we are already skipping CommandSender

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.getName().equalsIgnoreCase(sender.getName())) {
                    if (!clearChat(p, cc, true, false)) {
                        sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.ClearChat.ClearAll.Skipped")
                                .replace("%nick%", p.getDisplayName()));
                        skipped++;
                    } else {
                        cleared++;
                    }
                }
            }

            sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.ClearChat.ClearAll.Summary")
                    .replace("%cleared%", String.valueOf(cleared))
                    .replace("%skipped%", String.valueOf(skipped)));
        } else {

            if (!SCUtils.checkPermissions(sender, SCPermissionsEnum.CLEARCHAT_CLEARPLAYER.toString())) {
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                sender.sendMessage(ConfigUtils.getMessageFromMessagesFile(
                        "Commands.ClearChat.ClearSpecificPlayer.PlayerNotFound").replace("%nick%", args[1]));
                return;
            }

            if (!clearChat(target, cc, true, false))
                sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.ClearChat.ClearSpecificPlayer.ErrorImmunity")
                        .replace("%nick%", args[1]));
            else
                sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.ClearChat.ClearSpecificPlayer.Success")
                        .replace("%nick%", args[1]));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> ret = new ArrayList<>();

        if (args.length == 2) {
            ret.add("own");
            ret.add("all");

            for (Player p : Bukkit.getOnlinePlayers())
                ret.add(p.getName());
        } else if (args.length == 3) {
            ret.add("-a");
        }

        return ret;
    }

    private boolean clearChat(Player toClear, CommandSender sender, boolean sendMessage, boolean force) {

        if (!force && SCPermissionsEnum.CLEARCHAT_EXEMPT.hasPermission(toClear)) {
            return false;
        }

        for (int i = 0; i < ConfigUtils.getIntFromConfig("ClearChat.Lines.Player"); i++) {
            toClear.sendMessage(" ");
        }

        if (sendMessage) {
            if (sender == null) { // anonymous
                toClear.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.ClearChat.ChatCleared.AnonymousMode"));
            } else {
                toClear.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.ClearChat.ChatCleared.Normal")
                        .replace("%nick%", sender.getName()));
            }
        }
        return true;
    }
}