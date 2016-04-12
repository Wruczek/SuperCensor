package mr.wruczek.supercensor3.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import mr.wruczek.supercensor3.SCConfigManager2;
import mr.wruczek.supercensor3.SCMain;
import mr.wruczek.supercensor3.commands.SCMainCommand;
import mr.wruczek.supercensor3.commands.SCSubcommand;
import mr.wruczek.supercensor3.utils.ConfigUtils;
import mr.wruczek.supercensor3.utils.SCUtils;
import mr.wruczek.supercensor3.utils.classes.SCPermissionsEnum;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SCSelfMuteManager extends SCSubcommand implements Listener {

    public SCSelfMuteManager() {
        SCMainCommand.registerSubcommand(this, "selfmute", "sm");
        SCMainCommand.registerTabCompletion(this);
        Bukkit.getPluginManager().registerEvents(this, SCMain.getInstance());
    }

    private static List<String> selfMuted = new ArrayList<String>();

    public static void load() {
        selfMuted.clear();
        selfMuted = SCConfigManager2.data.getStringList("SelfmutedPlayers");
    }

    public static void save() {
        SCConfigManager2.data.set("SelfmutedPlayers", selfMuted);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void selfMuteListener(AsyncPlayerChatEvent event) {
        if (event.isCancelled() || selfMuted.isEmpty())
            return;

        // Little trick to avoid CurrentModificationException ;)
        for (Player recipient : new ArrayList<Player>(event.getRecipients())) {
            if (isSelfMuted(recipient) && !recipient.equals(event.getPlayer())) {
                event.getRecipients().remove(recipient);
            }
        }
    }

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {

        if (args.length > 1) {
            if (!SCUtils.checkPermissions(sender, SCPermissionsEnum.SELFMUTE_TOGGLE_OTHER.toString())) {
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.SelfMute.OtherPlayer.PlayerNotFound")
                        .replace("%nick%", args[1]));
                return;
            }

            if (SCPermissionsEnum.SELFMUTE_TOGGLE_OTHER_EXEMPT.hasPermission(target)) {
                sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.SelfMute.OtherPlayer.ErrorImmunity")
                        .replace("%nick%", target.getName()));
                return;
            }

            if (setSelfMute(target)) {
                sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.SelfMute.OtherPlayer.SelfMutedToSender")
                        .replace("%nick%", target.getName()));
                target.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.SelfMute.OtherPlayer.SelfMutedToTarget")
                        .replace("%nick%", sender.getName()));
            } else {
                sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.SelfMute.OtherPlayer.SelfMuteRemovedToSender")
                        .replace("%nick%", target.getName()));
                target.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.SelfMute.OtherPlayer.SelfMuteRemovedToTarget")
                        .replace("%nick%", sender.getName()));
            }
        } else {

            if (!(sender instanceof Player)) {
                sender.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.SelfMute.Self.OnlyPlayer"));
                return;
            }

            Player player = (Player) sender;

            if (!SCUtils.checkPermissions(player, SCPermissionsEnum.SELFMUTE_TOGGLE.toString())) {
                return;
            }

            if (setSelfMute(player)) {
                player.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.SelfMute.Self.SelfMuted"));
            } else {
                player.sendMessage(ConfigUtils.getMessageFromMessagesFile("Commands.SelfMute.Self.SelfMuteRemoved"));
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> ret = new ArrayList<>();

        if (args.length == 2) {
            for (Player p : Bukkit.getOnlinePlayers())
                ret.add(p.getName());
        }

        return ret;
    }

    public static boolean isSelfMuted(Player player) {
        return isSelfMuted(player.getName());
    }

    public static boolean isSelfMuted(String player) {
        for (String p : selfMuted) {
            if (p.equalsIgnoreCase(player)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see SCSelfMuteManager#setSelfMute(String)
     */
    public static boolean setSelfMute(Player player) {
        return setSelfMute(player.getName());
    }

    /**
     * Switches selfmute on given player
     *
     * @return Returns true if player is now selfmuted, otherwise false.
     */
    public static boolean setSelfMute(String player) {
        if (isSelfMuted(player)) {
            selfMuted.remove(player);
            return false;
        } else {
            selfMuted.add(player);
            return true;
        }
    }
}
