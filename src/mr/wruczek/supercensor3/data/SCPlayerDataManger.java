package mr.wruczek.supercensor3.data;

import java.io.File;
import java.io.IOException;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import mr.wruczek.supercensor3.SCMain;
import mr.wruczek.supercensor3.utils.LoggerUtils;
import mr.wruczek.supercensor3.utils.UUIDUtils;
import mr.wruczek.supercensor3.utils.classes.SCLogger;

public class SCPlayerDataManger {

    private File userFile;
    private OfflinePlayer player;
    private FileConfiguration config;
    public static String path;

    static {
        path = SCMain.getInstance().getDataFolder() + File.separator + "userdata" + File.separator;
    }

    public static FileConfiguration getPlayerConfig(OfflinePlayer player) {
        return new SCPlayerDataManger(player).getConfig();
    }

    public SCPlayerDataManger(OfflinePlayer player) {

        this.player = player;
        userFile = new File(path + getUUID());

        if (!userFile.exists()) {
            try {
                userFile.getParentFile().mkdirs();
                userFile.createNewFile();
            } catch (Exception e) {
                SCLogger.logInfo("Exception " + e.toString() + " while creating data file for player "
                        + player.getName() + " (UUID " + getUUID() + ")!", LoggerUtils.LogType.PLUGIN);
            }
        }

        config = YamlConfiguration.loadConfiguration(userFile);
    }

    private String getUUID() {
        return UUIDUtils.getUUID(player);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getName() {
        return player.getName();
    }

    public File getDataFile() {
        return userFile;
    }

    public void saveConfig() {
        try {
            config.save(userFile);
        } catch (IOException e) {
            SCLogger.logInfo("Exception " + e.toString() + " while creating data file for player "
                    + player.getName() + " (UUID " + getUUID() + ")!", LoggerUtils.LogType.PLUGIN);
        }
    }

    public static boolean hasDataFile(OfflinePlayer player) {
        return new File(path + UUIDUtils.getUUID(player)).exists();
    }

    public static File[] getAllDataFiles() {
        return new File(path).listFiles();
    }
}