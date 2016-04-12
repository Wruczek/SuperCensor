package mr.wruczek.supercensor3.PPUtils;

import java.util.Arrays;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import mr.wruczek.supercensor3.data.SCPlayerDataManger;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0
 * International License. http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class PPManager {

    // Methods
    public static int getPenaltyPoints(OfflinePlayer op) {

        if (!SCPlayerDataManger.hasDataFile(op))
            return 0;

        int penaltyPoints = 0;
        SCPlayerDataManger pdm = new SCPlayerDataManger(op);

        if (pdm.getConfig().contains("PenaltyPoints"))
            penaltyPoints = pdm.getConfig().getInt("PenaltyPoints");

        return penaltyPoints;
    }

    public static void setPenaltyPoints(OfflinePlayer player, int penaltyPoints) {
        setPenaltyPoints(player, penaltyPoints, false);
    }

    public static void setPenaltyPoints(OfflinePlayer player, int penaltyPoints, boolean runPPRCheck) {

        int oldPenaltyPoints = getPenaltyPoints(player);

        SCPlayerDataManger pdm = new SCPlayerDataManger(player);

        pdm.getConfig().set("PenaltyPoints", penaltyPoints);

        if (penaltyPoints <= 0)
            pdm.getConfig().set("PushmentList", Arrays.asList());

        pdm.saveConfig();

        if (runPPRCheck && player instanceof Player && player.isOnline()) // we can only punish online player
            PPCheck.checkPlayer((Player) player, penaltyPoints, oldPenaltyPoints);
    }

    public static void addPenaltyPoints(OfflinePlayer player, int penaltyPoints) {
        addPenaltyPoints(player, penaltyPoints, false);
    }

    public static void addPenaltyPoints(OfflinePlayer player, int penaltyPoints, boolean runPPRCheck) {
        setPenaltyPoints(player, getPenaltyPoints(player) + penaltyPoints, runPPRCheck);
    }

}
