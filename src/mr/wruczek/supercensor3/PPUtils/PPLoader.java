package mr.wruczek.supercensor3.PPUtils;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import mr.wruczek.supercensor3.utils.SCLogger.LogType;
import mr.wruczek.supercensor3.utils.SCUtils;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class PPLoader {
	
	public static ArrayList<ConfigurationSection> PPRules;
	
	public static void load(File folder) {
		
		PPRules = new ArrayList<>();
		
		for (File file : folder.listFiles()) {
			String path = file.getAbsolutePath();
			
			String fileType = path.substring(path.lastIndexOf('.') + 1);
			
			if (fileType.equalsIgnoreCase("yml")) {
				try {
					YamlConfiguration configFile = YamlConfiguration.loadConfiguration(new File(path));
					
					ConfigurationSection cs = configFile.getConfigurationSection("PenaltyPointsRules");

					if (cs != null)
						PPRules.add(cs);
					
					SCUtils.logInfo("Loaded " + file.getName() + " PenaltyPointsRules file", LogType.PLUGIN);
				} catch (Exception e) {
					SCUtils.logError("Error while loading " + path + ": " + e, LogType.PLUGIN);
				}
			}
		}
	}
	
}
