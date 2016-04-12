package mr.wruczek.supercensor3.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

import mr.wruczek.supercensor3.SCConfigManager2;
import mr.wruczek.supercensor3.SCMain;
import mr.wruczek.supercensor3.utils.classes.SCLogger;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0
 * International License. http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class LoggerUtils {

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

    public static Logger logger = Logger.getLogger("Minecraft");
    public static List<String> lastError;

    public static void handleException(Exception e) {

        lastError = new ArrayList<String>();

        logerror("");
        logerror("Exception in plugin SuperCensor");
        logerror(e.toString());

        if (e.getCause() != null)
            logerror(e.getCause().getMessage());

        logerror("");

        logerror("Server informations:");
        logerror("  " + SCMain.getInstance().getDescription().getFullName());
        logerror("  Server: " + Bukkit.getBukkitVersion() + " [" + Bukkit.getVersion() + "]");
        logerror("  Java: " + System.getProperty("java.version"));
        logerror("  Thread: " + Thread.currentThread());
        logerror("");

        logerror("StackTrace");
        logerror("");

        for (StackTraceElement stacktrace : e.getStackTrace()) {
            String st = stacktrace.toString();

            if (st.contains("mr.wruczek"))
                logerror("  @ > " + st);
            else
                logerror("  @ " + st);
        }

        logerror("");
        logerror("End of error");
        logerror("");
        logerror("If you want get help with this error:");
        logerror("  1. Run command \"SCreport\". It will send this error to hastebin,");
        logerror("  2. Use this link when creating new issue on BukkitDev.");
        logerror("");
    }

    private static void logerror(String err) {
        SCLogger.logError(err, LogType.PLUGIN);
        lastError.add(err);
    }

    public static void log(String text, LogType logType) {

        boolean sccmInit = true;
        boolean loggerEnabled = true;

        try {
            loggerEnabled = ConfigUtils.getBooleanFromConfig("Logger.Enabled");
            sccmInit = SCConfigManager2.isInitialized();
        } catch (Exception e) {
        }

        if (sccmInit && loggerEnabled) {

            FileWriter fw = null;
            PrintWriter pw = null;

            try {
                String prefix = "[%date% %time%] ";

                try {
                    prefix = ConfigUtils.getStringFromConfig("Logger.Prefix");
                } catch (Exception e) {
                }

                File logFile = SCLogger.getLogFile(logType.getFileName());

                if (!logFile.exists()) {
                    logFile.getParentFile().mkdirs();
                    logFile.createNewFile();
                }

                fw = new FileWriter(logFile, true);
                pw = new PrintWriter(fw);
                pw.println(prefix.replace("%date%", getDate()).replace("%time%", getTime()) + StringUtils.unColor(text));
                pw.flush();
            } catch (Exception e) {
                handleException(e);
            } finally {
                try {
                    pw.close();
                    fw.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public static String getTime() {

        String timeFormat = "HH:mm:ss";

        try {
            timeFormat = ConfigUtils.getStringFromConfig("Logger.TimeFormat");
        } catch (Exception e) {
        }

        return new SimpleDateFormat(timeFormat).format(new Date());
    }

    public static String getDate() {

        String dateFormat = "dd-MM-yyyy";

        try {
            dateFormat = ConfigUtils.getStringFromConfig("Logger.DateFormat");
        } catch (Exception e) {
        }

        return new SimpleDateFormat(dateFormat).format(new Date());
    }

}
