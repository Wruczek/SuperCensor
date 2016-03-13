package mr.wruczek.supercensor3.data;

import java.io.File;
import java.io.IOException;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import mr.wruczek.supercensor3.SCMain;
import mr.wruczek.supercensor3.utils.SCLogger.LogType;
import mr.wruczek.supercensor3.utils.SCUtils;

public class SCPlayerDataManger {

	private File userFile;
	private OfflinePlayer player;
	private FileConfiguration config;
	public static String path;

	static {
		path = SCMain.getInstance().getDataFolder() + File.separator + "userdata" + File.separator;
	}

	public static FileConfiguration getPlayerConfig(Player player) {
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
				SCUtils.logInfo("Exception " + e.toString() + " while creating data file for player " 
						+ player.getName() + " (UUID " + getUUID() + ")!", LogType.PLUGIN);
			}
		}

		config = YamlConfiguration.loadConfiguration(userFile);
	}

	private String getUUID() {
		return SCUtils.getUUID(player);
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
			SCUtils.logInfo("Exception " + e.toString() + " while creating data file for player " 
					+ player.getName() + " (UUID " + getUUID() + ")!", LogType.PLUGIN);
		}
	}

	public static boolean hasDataFile(OfflinePlayer player) {
		return new File(path + SCUtils.getUUID(player)).exists();
	}

	public static File[] getAllDataFiles() {
		return new File(path).listFiles();
	}
}