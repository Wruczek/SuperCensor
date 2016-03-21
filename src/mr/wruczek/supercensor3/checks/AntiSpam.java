package mr.wruczek.supercensor3.checks;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import mr.wruczek.supercensor3.SCCheckEvent;
import mr.wruczek.supercensor3.SCMain;
import mr.wruczek.supercensor3.PPUtils.PPManager;
import mr.wruczek.supercensor3.utils.ConfigUtils;
import mr.wruczek.supercensor3.utils.classes.SCPermissionsEnum;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0
 * International License. http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class AntiSpam implements Listener {

	private static ArrayList<AntiSpamData> data = new ArrayList<>();
	
	public static int schedulerId = 0;
	
	public AntiSpam() {
		
		if(!ConfigUtils.getBooleanFromConfig("AntiSpam.AntiSpam.Enabled"))
			return;
		
		Bukkit.getScheduler().cancelTask(schedulerId);
		schedulerId = runWarnScheduler();
	}
	
	@EventHandler
	public void checkListener(final SCCheckEvent event) {

		if (event.isCensored())
			return;

		boolean enabledAntiSpam = ConfigUtils.getBooleanFromConfig("AntiSpam.AntiSpam.Enabled");
		boolean enabledAntiRepeat = ConfigUtils.getBooleanFromConfig("AntiSpam.AntiRepeat.Enabled");
		
		if(!enabledAntiSpam && !enabledAntiRepeat) {
			return;
		}
		
		AntiSpamData data = getPlayerData(event.getPlayer());
		
		if(data == null) {
			addPlayerData(event.getPlayer(), event.getOriginalMessage());
			return;
		}
		
		if(enabledAntiSpam && !SCPermissionsEnum.ANTISPAM_BYPASS.hasPermission(event.getPlayer())) {
			
			long minimumMessageRepeatTime = ConfigUtils.getIntFromConfig("AntiSpam.AntiSpam.MinimumMessageRepeatTime");
			int allowedNumberOfWarns = ConfigUtils.getIntFromConfig("AntiSpam.AntiSpam.MaximumNumberOfWarns");
			int penaltyPoints = ConfigUtils.getIntFromConfig("AntiSpam.AntiSpam.PenaltyPoints");
			
			long time = data.getTime();
			data.setLastMessageTime();
			
			if(time <= minimumMessageRepeatTime) {
				data.addWarn();
			}
			
			if(data.getWarns() >= allowedNumberOfWarns) {
				
				Bukkit.getScheduler().runTask(SCMain.getInstance(), new Runnable() {
					@Override
					public void run() {
						event.getPlayer().kickPlayer(ConfigUtils.getMessageFromMessagesFile("AntiSpam.KickMessage"));
					}
				});
				
				PPManager.addPenaltyPoints(event.getPlayer(), penaltyPoints, true);
				data.setWarns(0);
				return;
			}
		}
		
		if(enabledAntiRepeat && !SCPermissionsEnum.ANTIREPEAT_BYPASS.hasPermission(event.getPlayer())) {
			if(data.getLastMessage().equalsIgnoreCase(event.getOriginalMessage()))
				data.addRepeat();
			else {
				data.setRepeats(0);
				data.setLastMessage(event.getOriginalMessage());
				return;
			}
			
			int allowedRepeats = ConfigUtils.getIntFromConfig("AntiSpam.AntiRepeat.AllowedRepeats");
			int penaltyPoints = ConfigUtils.getIntFromConfig("AntiSpam.AntiRepeat.PenaltyPoints");
			boolean cancelMessage = ConfigUtils.getBooleanFromConfig("AntiSpam.AntiRepeat.CancelMessage");
			
			if(data.getRepeats() >= allowedRepeats && !event.isCensored()) {
				
				event.getPlayer().sendMessage(ConfigUtils.getMessageFromMessagesFile("AntiSpam.RepeatMessage"));
				
				if(cancelMessage)
					event.setCensored(true);
				
				PPManager.addPenaltyPoints(event.getPlayer(), penaltyPoints, true);
			}
		}
		
	}
	
	public static ArrayList<AntiSpamData> getData() {
		return data;
	}
	
	public static void addPlayerData(Player player, String lastMessage) {
		if (getPlayerData(player) != null)
			return;
		
		getData().add(new AntiSpamData(player, lastMessage));
	}
	
	public static AntiSpamData getPlayerData(Player player) {
		for(AntiSpamData d : data) {
			if(d.getPlayer().getName().equalsIgnoreCase(player.getName()))
				return d;
		}
		
		return null;
	}

	public static void removePlayerFromMap(Player player) {
		
		AntiSpamData toRemove = null;
		
		for(AntiSpamData d : data) {
			if(d.getPlayer().getName().equalsIgnoreCase(player.getName())) {
				toRemove = d;
				break;
			}
		}
		
		// Avoiding CurrentModificationException
		if(toRemove != null)
			data.remove(toRemove);
	}
	
	public static int runWarnScheduler() {
		
		long removeWarnEvery = ConfigUtils.getIntFromConfig("AntiSpam.AntiSpam.RemoveWarnEvery");
		
		if (removeWarnEvery == 0)
			return -1;
		
		return Bukkit.getScheduler().scheduleSyncRepeatingTask(SCMain.getInstance(), new Runnable() {
			@Override
			public void run() {
				for(AntiSpamData data : AntiSpam.getData()) {
					data.removeWarn();
				}
			}
		}, 20L * removeWarnEvery, 20L * removeWarnEvery);
	}
}
