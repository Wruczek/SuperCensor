package mr.wruczek.supercensor3.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import mr.wruczek.supercensor3.SCConfigManager2;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SCLogger {
	
	public enum LogType {

		CHAT("chat.txt"), CENSOR("censor.txt"), PLUGIN("plugin.txt");

		private final String fileName;

		private LogType(String fileName) {
			this.fileName = fileName;
		}

		public String getFileName() {
			return fileName;
		}
	}
	
	public static void log(String text, LogType logType) {
		if (SCConfigManager2.isInitialized() && SCConfigManager2.config.getBoolean("Logger.Enabled")) {
			try {
				
				File logFile = getLogFile(logType.getFileName());
				
				if(!logFile.exists()) {
					logFile.getParentFile().mkdirs();
					logFile.createNewFile();
				}
				
				FileWriter fw = new FileWriter(logFile, true);
				PrintWriter pw = new PrintWriter(fw);
				pw.println(SCConfigManager2.config.getString("Logger.Prefix")
						.replace("%date%", getDate())
						.replace("%time%", getTime())
						+ SCUtils.unColor(text));
				pw.flush();
				pw.close();
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getDate() {
	    DateFormat dateFormat = new SimpleDateFormat(SCConfigManager2.config.getString("Logger.DateFormat"));
	    return dateFormat.format(new Date());
	}
	
	public static String getTime() {
		DateFormat godzina = new SimpleDateFormat(SCConfigManager2.config.getString("Logger.TimeFormat"));
		return godzina.format(new Date());
	}
	
	public static File getLogFile(String fileName) {
		return new File(SCConfigManager2.logsFolder + File.separator + getDate(), fileName);
	}
}