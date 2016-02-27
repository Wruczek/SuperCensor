package mr.wruczek.supercensor3.data;

import java.io.File;
import java.io.IOException;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import mr.wruczek.supercensor3.SCMain;
import mr.wruczek.supercensor3.utils.SCLogger.LogType;
import mr.wruczek.supercensor3.utils.SCUtils;

public class SCPlayerDataManger {

	private File userFile;
	private OfflinePlayer player;
	private FileConfiguration config;
	public static String path;

	public static void init() {
		path = SCMain.getInstance().getDataFolder() + File.separator + "userdata" + File.separator;
	}

	public SCPlayerDataManger(OfflinePlayer player) {

		init();

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
		init();
		return new File(path + SCUtils.getUUID(player)).exists();
	}

	public static File[] getAllDataFiles() {
		init();
		return new File(path).listFiles();
	}

	// Methods
	public int getPenalityPoints() {
		int penalityPoints = 0;

		if (getConfig().contains("PenalityPoints"))
			penalityPoints = getConfig().getInt("PenalityPoints");

		return penalityPoints;
	}

	public void setPenalityPoints(int penalityPoints) {
		getConfig().set("PenalityPoints", penalityPoints);
		saveConfig();
	}

	public void addPenalityPoints(int penalityPoints) {
		setPenalityPoints(getPenalityPoints() + penalityPoints);
	}
}