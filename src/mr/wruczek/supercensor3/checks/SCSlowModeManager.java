package mr.wruczek.supercensor3.checks;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import mr.wruczek.supercensor3.SCConfigManager2;
import mr.wruczek.supercensor3.utils.ConfigUtils;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SCSlowModeManager {

    public static SCSlowModeManager getManager = new SCSlowModeManager();

    private Map<String, Long> map = new HashMap<String, Long>();

    public boolean isEnabled() {
        if (!SCConfigManager2.isInitialized())
            throw new IllegalStateException("SCConfigManager2 is not initialized!");

        return ConfigUtils.getBooleanFromConfig("SlowMode.Enabled");
    }

    public long getSlowModeTimeLeftInMillis(Player player) {
        return getSlowModeTimeLeftInMillis(player.getName());
    }

    public long getSlowModeTimeLeftInMillis(String player) {
        Entry<String, Long> playerEntry = getPlayerEntry(player);

        if (playerEntry == null)
            return 0;

        long time = getSlowModeTimeInSeconds() - (System.currentTimeMillis() - playerEntry.getValue());

        if (time < 0)
            return 0;
        else
            return time;
    }

    public void addToMap(Player player) {
        addToMap(player.getName());
    }

    public void addToMap(String player) {
        removeFromMap(player);
        getMap().put(player, System.currentTimeMillis());
    }

    public void removeFromMap(Player player) {
        removeFromMap(player.getName());
    }

    public void removeFromMap(String player) {
        Entry<String, Long> entry = getPlayerEntry(player);

        if (entry != null)
            getMap().remove(entry.getKey());
    }

    public Map<String, Long> getMap() {
        return map;
    }

    public void clearMap() {
        getMap().clear();
    }

    public int getSlowModeTimeInSeconds() {
        if (!SCConfigManager2.isInitialized())
            throw new IllegalStateException("SCConfigManager2 is not initialized!");

        if (!isEnabled())
            return 0;

        return (ConfigUtils.getIntFromConfig("SlowMode.Time") + 1) * 1000;
    }

    public Entry<String, Long> getPlayerEntry(String player) {
        for (Entry<String, Long> entry : getMap().entrySet())
            if (entry.getKey().equalsIgnoreCase(player))
                return entry;
        return null;
    }
}