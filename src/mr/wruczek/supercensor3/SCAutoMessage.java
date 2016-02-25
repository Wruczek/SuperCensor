package mr.wruczek.supercensor3;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import mr.wruczek.supercensor3.utils.SCUtils;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SCAutoMessage {
	
	private static int schedulerId;
	private static int messageId;
	private static List<String> messagesList;
	
	public static boolean isEnabled() {
		return SCConfigManager2.config.getBoolean("FunStuff.AutoMessage.Enabled");
	}
	
	public static int getIntervalInSeconds() {
		return SCConfigManager2.config.getInt("FunStuff.AutoMessage.Interval");
	}
	
	public static boolean displayInConsole() {
		return SCConfigManager2.config.getBoolean("FunStuff.AutoMessage.DisplayInConsole");
	}
	
	public static boolean pauseOnEmptyServer() {
		return SCConfigManager2.config.getBoolean("FunStuff.AutoMessage.PauseOnEmptyServer");
	}
	
	public static boolean playNiceSoundOnDisplay() {
		return SCConfigManager2.config.getBoolean("FunStuff.AutoMessage.PlayNiceSoundOnDisplay");
	}
	
	public static void run() {
		
		if(schedulerId != 0)
			Bukkit.getScheduler().cancelTask(schedulerId);
		
		if(!isEnabled())
			return;
		
		messagesList = SCConfigManager2.config.getStringList("FunStuff.AutoMessage.Messages");
		messageId = 0;
		
		schedulerId = Bukkit.getScheduler().scheduleSyncRepeatingTask(SCMain.getInstance(), new Runnable() {
			public void run() {
				
				if(pauseOnEmptyServer() && SCUtils.getNumberOfPlayersOnline() == 0)
					return;
				
				String message = SCUtils.color(getNextMessage());
				
				for (Player player : Bukkit.getOnlinePlayers()) {
					player.sendMessage(message.replace("%nick%", player.getName()));
					
					if(playNiceSoundOnDisplay())
						player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);
				}
				
				if(displayInConsole())
					// New lines in console is waste of space, and it doesn't look good. Lets remove it
					SCUtils.logInfo(SCUtils.unColor(message.replace("\n", " ").replace("%nick%", "CONSOLE")));
			}
		}, getIntervalInSeconds() * 20L, getIntervalInSeconds() * 20L);
	}

	public static String getNextMessage() {
		if ((messageId + 1) > messagesList.size()) {
			messageId = 0;
		}
		
		return messagesList.get(messageId++);
	}
}
