package mr.wruczek.supercensor3.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.evilmidget38.UUIDFetcher;

import mr.wruczek.supercensor3.SCConfigManager2;
import mr.wruczek.supercensor3.SCMain;
import mr.wruczek.supercensor3.utils.SCLogger.LogType;
import net.gravitydevelopment.updater.GravityUpdater;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SCUtils {
	
	private static Logger logger = Logger.getLogger("Minecraft");
	
	public static String getPluginPrefix() {
		return color("&8[&6SC&8]&7 ");
	}
	
	public static String getLogPrefix() {
		return unColor(getPluginPrefix());
	}
	
	public static Logger getLogger() {
		return logger;
	}
	
	public static void logInfo(String str) {
		logger.info(getLogPrefix() + unColor(str));
	}
	
	public static void logInfo(String str, LogType logType) {
		logInfo(str);
		SCLogger.log(str, logType);
	}
	
	public static void logWarning(String str) {
		logger.warning(getLogPrefix() + unColor(str));
	}
	
	public static void logWarning(String str, LogType logType) {
		logWarning(str);
		SCLogger.log(str, logType);
	}
	
	public static void logError(String str) {
		logger.severe(getLogPrefix() + unColor(str));
	}
	
	public static void logError(String str, LogType logType) {
		logError(str);
		SCLogger.log(str, logType);
	}
	
	public static String color(String str) {
		if (str == null)
			return null;
		
		if(SCConfigManager2.isInitialized() && SCConfigManager2.pluginPrefix != null) {
			str = str.replace("%prefix%", SCConfigManager2.pluginPrefix);
		}
		
		return ChatColor.translateAlternateColorCodes('&', str);
	}
	
	public static String unColor(String str) {
		return ChatColor.stripColor(str);
	}
	
	public static String getUUID(Player player) {
		return getUUID(player.getName());
	}
	
	public static String getUUID(String player) {
		try {
			return UUIDFetcher.getUUIDOf(player).toString().replaceAll("-", "");
		} catch (Exception e) {
			logError("Cant get UUID of " + player + "! Exception: " + e);
		}
		return null;
	}
	
	public static String getMessageFromMessagesFile(String path) {
		if(SCConfigManager2.messages.contains(path)) {
			return SCConfigManager2.messages.getColored(path);
		}
		
		if (SCConfigManager2.messages_original.contains(path)) {
			logWarning("Cannot find " + path + " in original messages.yml file, getting it from jar file...");
			String original = SCUtils.color(SCConfigManager2.messages_original.getString(path));
			SCConfigManager2.messages.set(path, original);
			SCConfigManager2.messages.save();
			return original;
		}
		
		return getPluginPrefix() + color("&cFatal error: message " + path + " cannot be found!");
	}
	
	// region GetFromConfig
	public static Object getFromConfig(String path) {
		if(SCConfigManager2.config.contains(path)) {
			return SCConfigManager2.config.get(path);
		}
		
		if (SCConfigManager2.config_original.contains(path)) {
			logWarning("Cannot find " + path + " in original config.yml file, getting it from jar file...");
			Object original = SCConfigManager2.config_original.get(path);
			SCConfigManager2.config.set(path, original);
			SCConfigManager2.config.save();
			return original;
		}
		
		return getPluginPrefix() + color("&cFatal error: config variable " + path + " cannot be found!");
	}
	
	public static String getStringFromConfig(String path) {
		return SCUtils.color((String) getFromConfig(path));
	}
	
	public static boolean getBooleanFromConfig(String path) {
		return (boolean) getFromConfig(path);
	}
	
	public static int getIntFromConfig(String path) {
		return (int) getFromConfig(path);
	}
	
	public static double getDoubleFromConfig(String path) {
		return (double) getFromConfig(path);
	}
	// endregion
	
	public static String formatUpdaterMessage(String message) {
		GravityUpdater updater = SCUpdater.instance.getUpdater();
		
		if(updater == null) return null;
		
		return message.replace("%updaterprefix%", SCConfigManager2.config.getColored("MessageFormat.UpdaterPrefix"))
				.replace("%currentversion%", SCMain.getInstance().getDescription().getFullName())
				.replace("%newversion%", updater.getLatestName())
				.replace("%downloadlink%", updater.getLatestFileLink());
	}
	
	public static String usageFormatter(String command, String... args) {
		if(command == null || args == null) return null;
		
		String prefix = SCConfigManager2.config.getColored("MessageFormat.CommandUsageFormat").replace("%command%", command);
		
		StringBuilder usageBuilder = new StringBuilder();
		
		for(String str : args) {
			if(str.startsWith("!")) {
				usageBuilder.append("&8[&6" + str.substring(1) + "&8] ");
			} else {
				usageBuilder.append("&8<&6" + str + "&8> ");
			}
		}
		
		String usage = usageBuilder.toString();
		usage = usage.substring(0, usage.length() - 1);
		
		return color(prefix.replace("%usage%", usage));
	}
	
	public static boolean checkForPermissions(CommandSender sender, String permission) {
		if(sender.hasPermission(permission)) {
			return true;
		}
		
		sender.sendMessage(getMessageFromMessagesFile("Commands.NoPermissions").replace("%permission%", permission));
		return false;
	}
	
	public static String argsToString(String[] args, int startFrom) {
		
		if(args.length == 0){
			return "";
		}
		
		String ret = "";
		
		for (int i = startFrom; i < args.length; i++) {
			ret += args[i] + " ";
		}

		return ret.substring(0, ret.length() - 1);
	}
	
	/**
	 * Credits: http://stackoverflow.com/a/12026782
	 */
	public static String replaceIgnoreCase(String source, String target, String replacement) {
		StringBuilder sbSource = new StringBuilder(source);
		StringBuilder sbSourceLower = new StringBuilder(source.toLowerCase());
		String searchString = target.toLowerCase();

		int idx = 0;
		while ((idx = sbSourceLower.indexOf(searchString, idx)) != -1) {
			sbSource.replace(idx, idx + searchString.length(), replacement);
			sbSourceLower.replace(idx, idx + searchString.length(), replacement);
			idx += replacement.length();
		}
		sbSourceLower.setLength(0);
		sbSourceLower.trimToSize();
		sbSourceLower = null;

		return sbSource.toString();
	}
	
	public static boolean checkRegex(String regex, String string) {
		return Pattern.compile(regex).matcher(string).find();
	}
	
	public static double getCapsPercent(String str) {
		int total = 0;
		int uppers = 0;

		for (char c : str.toCharArray()) {
			
			if(!Character.isAlphabetic(c))
				continue;
			
			total++;
			
			if (Character.isUpperCase(c)) {
				uppers++;
			}
		}
		
		return (uppers * 1D) / (total * 1D) * 100D;
	}
	
	public static void sendCommandUsage(CommandSender sender, String command, String descriptionPath) {
		
		String description = SCUtils.getMessageFromMessagesFile(descriptionPath);
		
		if(SCUtils.isTellrawSupported(sender)) {
			String commandUsageFormat = SCConfigManager2.config.getColored("MessageFormat.HelpEntryFormat");
			commandUsageFormat = SCUtils.color(commandUsageFormat.replace("%command%", command));
			
			sendTellraw((Player)sender, commandUsageFormat, description, SCUtils.unColor(commandUsageFormat.replace("- ", "")));
			return;
		}
		
		String commandUsageFormat = SCConfigManager2.config.getColored("MessageFormat.OldHelpEntryFormat");
		
		sender.sendMessage(SCUtils.color(commandUsageFormat.replace("%command%", command).replace("%description%", description)));
	}
	
	public static void sendTellraw(Player player, String message, String hovertext) {
		sendTellraw(player, message, hovertext, "");
	}
	
	public static void sendTellraw(Player player, String message, String hovertext, String suggestedCommand) {
		String json = "[\"\",{\"text\":\"" + message + "\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + suggestedCommand + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + hovertext + "\"}]}}}]";
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:tellraw " + player.getName() + " " + json);
	}
	
	public static String getCommandDescription(String descriptionPath) {
		return SCUtils.getMessageFromMessagesFile("Commands.Description") + SCUtils.getMessageFromMessagesFile(descriptionPath);
	}
	
	public static boolean isTellrawSupported(CommandSender sender) {
		String version = Bukkit.getBukkitVersion();
		return (version.startsWith("1.9") || version.startsWith("1.8") || version.startsWith("1.7")) && sender instanceof Player;
	}
	
	// This is simple workaround over 1.8 changes.
	public static int getNumberOfPlayersOnline() {
		int i = 0;
		for (@SuppressWarnings("unused") Player player : Bukkit.getOnlinePlayers())
			i++;
		return i;
	}
	
	public static void copyResource(String resourcePath, File saveTo, boolean force) throws IOException {
			InputStream in = SCMain.getInstance().getResource(resourcePath);
			
			if (in == null) {
				throw new IllegalArgumentException("Resource \"" + resourcePath + "\" not found!");
			}
			
			if(saveTo.exists() && !force) return;
			
			File parent = saveTo.getParentFile();
			if (!(parent.exists())) {
				parent.mkdirs();
			}
			
			OutputStream out = new FileOutputStream(saveTo);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
			
			logInfo("Created new file from resource: " + saveTo.getName());
	}

}