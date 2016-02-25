package mr.wruczek.supercensor3.data;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import mr.wruczek.supercensor3.SCMain;
import mr.wruczek.supercensor3.utils.SCLogger.LogType;
import mr.wruczek.supercensor3.utils.SCUtils;

public class SCPlayerDataManger {

	private File userFile;
	private String nick;
	private FileConfiguration config;
	public static String path;
	
	public static void init() {
		path = SCMain.getInstance().getDataFolder() + File.separator + "userdata" + File.separator;
	}
	
	public SCPlayerDataManger(String nick) {
		
		init();
		
		this.nick = nick;
		userFile = new File(path + nick.toLowerCase());

		if (!userFile.exists()) {
			try {
				userFile.getParentFile().mkdirs();
				userFile.createNewFile();
			} catch (Exception e) {
				SCUtils.logInfo("Exception " + e.toString() + 
						" while creating data file for player " + nick + "!", LogType.PLUGIN);
			}
		}

		config = YamlConfiguration.loadConfiguration(userFile);
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public String getName() {
		return nick;
	}

	public File getDataFile() {
		return userFile;
	}

	public void saveConfig() {
		try {
			config.save(userFile);
		} catch (IOException e) {
			SCUtils.logInfo("Exception " + e.toString() + 
					" while saving data file for player " + nick + "!", LogType.PLUGIN);
		}
	}

	public static boolean hasDataFile(String nick) {
		init();
		return new File(path + nick.toLowerCase()).exists();
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