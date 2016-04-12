package mr.wruczek.supercensor3.utils.classes;

import org.bukkit.command.CommandSender;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public enum SCPermissionsEnum {

    PERMISSIONPREFIX("supercensor.", "Prefix for all other permissions"),
    BASICADMIN("supercensor.basicadmin", "Administrative permission for less important "
            + "things like updater notifications and /screport command"),
    RELOAD("reload", "Access to /sc reload command"),
    INFO("info", "Access to /sc info command"),
    CLEARCHAT_CLEAROWN("clearchat.clearown", "Access to /sc clear own command"),
    CLEARCHAT_CLEARPLAYER("clearchat.clearplayer", "Access to /sc clear <playername> command"),
    CLEARCHAT_CLEARALL("clearchat.clearall", "Access to /sc clear all command"),
    CLEARCHAT_EXEMPT("clearchat.exempt", "Players with this permission will not have chat cleaned"),
    CLEARCHAT_ANONYMOUS("clearchat.anonymous", "Access to anonymous chat cleaning (-a argument)"),
    MUTECHAT_TOGGLE("mutechat.toggle", "Access to /sc mute command"),
    MUTECHAT_EXEMPT("mutechat.exempt", "Allow to write when chat is disabled"),
    SELFMUTE_TOGGLE("selfmute.toggle", "Access to /sc selfmute"),
    SELFMUTE_TOGGLE_OTHER("selfmute.toggle.other", "Allows to block other players chat by /sc selfmute <playername>"),
    SELFMUTE_TOGGLE_OTHER_EXEMPT("selfmute.toggle.other.exempt", "Players with this permission cannot be "
            + "selfmuted using /sc selfmute <playername> command"),
    PPM("ppm", "Access to PenaltyPoints manager (/sc ppm)"),
    SLOWMODE_BYPASS("bypass.slowmode", "Exempts from chat SlowMode"),
    WORDLIST_BYPASS("bypass.wordlist", "Exempts from Wordlist"),
    ANTIREPEAT_BYPASS("bypass.antirepeat", "Exempts from AntiRepeat"),
    ANTISPAM_BYPASS("bypass.antispam", "Exempts from AntiSpam");

    private final String permission;
    private final String description;

    private SCPermissionsEnum(final String permission, String description) {
        this.permission = permission;
        this.description = description;
    }

    @Override
    public String toString() {
        return (PERMISSIONPREFIX.permission + permission).toLowerCase();
    }

    public String getPermission() {
        return permission;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(toString());
    }
}
