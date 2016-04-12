package mr.wruczek.supercensor3.utils.classes;

import java.io.File;
import java.util.logging.Logger;

import mr.wruczek.supercensor3.SCConfigManager2;
import mr.wruczek.supercensor3.utils.LoggerUtils;
import mr.wruczek.supercensor3.utils.SCUtils;
import mr.wruczek.supercensor3.utils.StringUtils;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0
 * International License. http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SCLogger {

    public static File getLogFile(String fileName) {
        return new File(SCConfigManager2.logsFolder + File.separator + LoggerUtils.getDate(), fileName);
    }

    public static Logger getLogger() {
        return LoggerUtils.logger;
    }

    public static void logInfo(String str) {
        LoggerUtils.logger.info(SCUtils.getLogPrefix() + StringUtils.unColor(str));
    }

    public static void logError(String str, LoggerUtils.LogType logType) {
        logError(str);
        LoggerUtils.log(str, logType);
    }

    public static void logError(String str) {
        LoggerUtils.logger.severe(SCUtils.getLogPrefix() + StringUtils.unColor(str));
    }

    public static void logWarning(String str, LoggerUtils.LogType logType) {
        logWarning(str);
        LoggerUtils.log(str, logType);
    }

    public static void logWarning(String str) {
        LoggerUtils.logger.warning(SCUtils.getLogPrefix() + StringUtils.unColor(str));
    }

    public static void logInfo(String str, LoggerUtils.LogType logType) {
        logInfo(str);
        LoggerUtils.log(str, logType);
    }
}
