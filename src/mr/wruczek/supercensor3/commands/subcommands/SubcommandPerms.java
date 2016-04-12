package mr.wruczek.supercensor3.commands.subcommands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mr.wruczek.supercensor3.commands.SCCommandHeader;
import mr.wruczek.supercensor3.commands.SCMainCommand;
import mr.wruczek.supercensor3.commands.SCSubcommand;
import mr.wruczek.supercensor3.utils.ConfigUtils;
import mr.wruczek.supercensor3.utils.TellrawUtils;
import mr.wruczek.supercensor3.utils.classes.SCPermissionsEnum;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0
 * International License. http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SubcommandPerms extends SCSubcommand {

    public SubcommandPerms() {
        SCMainCommand.registerSubcommand(this, "perms", "permissions");
        SCMainCommand.registerTabCompletion(this);
    }

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {

        if (!(command.equalsIgnoreCase("perms") || command.equalsIgnoreCase("permissions"))) {
            return;
        }

        sender.sendMessage(SCCommandHeader.getHeader());

        sender.sendMessage("\n");

        if (TellrawUtils.isTellrawSupported(sender)) {
            // Later will be changed to reflections

            for (SCPermissionsEnum permission : SCPermissionsEnum.values()) {
                if (permission != SCPermissionsEnum.PERMISSIONPREFIX) {
                    TellrawUtils.sendTellraw((Player) sender, format(permission.toString()), permission.getDescription());
                }
            }

            sender.sendMessage("\n" + ConfigUtils.getMessageFromMessagesFile("Commands.HoverPermissionTip"));
        } else {
            // For console / older Minecraft versions

            for (SCPermissionsEnum permission : SCPermissionsEnum.values()) {
                if (permission != SCPermissionsEnum.PERMISSIONPREFIX)
                    sender.sendMessage(oldFormat(permission.toString(), permission.getDescription()));
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Arrays.asList(); // no arguments
    }

    private String format(String permission) {
        return ConfigUtils.getColoredStringFromConfig("MessageFormat.PermissionEntryFormat")
                .replace("%permission%", permission);
    }

    private String oldFormat(String permission, String description) {
        return ConfigUtils.getColoredStringFromConfig("MessageFormat.OldPermissionEntryFormat")
                .replace("%permission%", permission).replace("%description%", description);
    }
}