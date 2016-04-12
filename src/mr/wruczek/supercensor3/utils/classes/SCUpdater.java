package mr.wruczek.supercensor3.utils.classes;

import mr.wruczek.supercensor3.SCConfigManager2;
import mr.wruczek.supercensor3.SCMain;
import mr.wruczek.supercensor3.utils.ConfigUtils;
import mr.wruczek.supercensor3.utils.classes.GravityUpdater.UpdateResult;
import mr.wruczek.supercensor3.utils.classes.GravityUpdater.UpdateType;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SCUpdater {

    public static SCUpdater instance = new SCUpdater();
    private static GravityUpdater updater;
    private boolean updateAvailable;

    private SCUpdater() {

        if (!SCConfigManager2.isInitialized())
            throw new IllegalStateException("You must initialize SCConfigManager fitst!");

        if (isUpdaterEnabled())
            initUpdater();
    }

    public GravityUpdater getUpdater() {
        return updater;
    }

    public UpdateResult checkForUpdates() {
        return getResult();
    }

    public UpdateResult getResult() {

        if (updater == null)
            return UpdateResult.DISABLED;

        UpdateResult res = getUpdater().getResult();
        setUpdateAvailable(res == UpdateResult.UPDATE_AVAILABLE || res == UpdateResult.SUCCESS);
        return res;
    }

    public boolean isUpdateAvailable() {
        if (updater == null)
            return false;

        return updateAvailable;
    }

    public void setUpdateAvailable(boolean updateAvailable) {
        this.updateAvailable = updateAvailable;
    }

    public boolean isUpdaterEnabled() {
        return ConfigUtils.getBooleanFromConfig("AutoUpdater.Enabled");
    }

    public UpdateType getUpdateType() {
        return ConfigUtils.getBooleanFromConfig("AutoUpdater.AutoDownload") ?
                GravityUpdater.UpdateType.DEFAULT : GravityUpdater.UpdateType.NO_DOWNLOAD;
    }

    private void initUpdater() {
        updater = new GravityUpdater(SCMain.getInstance(), 56448, SCMain.getPluginFile(), getUpdateType(), true);
    }

}
