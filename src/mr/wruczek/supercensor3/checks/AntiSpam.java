package mr.wruczek.supercensor3.checks;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import mr.wruczek.supercensor3.SCCheckEvent;
import mr.wruczek.supercensor3.SCConfigManager2;
import mr.wruczek.supercensor3.SCMain;
import mr.wruczek.supercensor3.PPUtils.PPManager;
import mr.wruczek.supercensor3.utils.SCPermissionsEnum;
import mr.wruczek.supercensor3.utils.SCUtils;

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
		
		if(!SCConfigManager2.config.getBoolean("AntiSpam.AntiSpam.Enabled"))
			return;
		
		Bukkit.getScheduler().cancelTask(schedulerId);
		schedulerId = runWarnScheduler();
	}
	
	@EventHandler
	public void checkListener(final SCCheckEvent event) {

		if (event.isCensored())
			return;

		boolean enabledAntiSpam = SCConfigManager2.config.getBoolean("AntiSpam.AntiSpam.Enabled");
		boolean enabledAntiRepeat = SCConfigManager2.config.getBoolean("AntiSpam.AntiRepeat.Enabled");
		
		if(!enabledAntiSpam && !enabledAntiRepeat) {
			return;
		}
		
		AntiSpamData data = getPlayerData(event.getPlayer());
		
		if(data == null) {
			addPlayerData(event.getPlayer(), event.getOriginalMessage());
			return;
		}
		
		if(enabledAntiSpam && !SCPermissionsEnum.ANTISPAM_BYPASS.hasPermission(event.getPlayer())) {
			
			long minimumMessageRepeatTime = SCConfigManager2.config.getLong("AntiSpam.AntiSpam.MinimumMessageRepeatTime");
			int allowedNumberOfWarns = SCConfigManager2.config.getInt("AntiSpam.AntiSpam.MaximumNumberOfWarns");
			int penaltyPoints = SCConfigManager2.config.getInt("AntiSpam.AntiSpam.PenaltyPoints");
			
			long time = data.getTime();
			data.setLastMessageTime();
			
			if(time <= minimumMessageRepeatTime) {
				data.addWarn();
			}
			
			if(data.getWarns() >= allowedNumberOfWarns) {
				
				Bukkit.getScheduler().runTask(SCMain.getInstance(), new Runnable() {
					@Override
					public void run() {
						event.getPlayer().kickPlayer(SCUtils.getMessageFromMessagesFile("AntiSpam.KickMessage"));
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
			
			int allowedRepeats = SCConfigManager2.config.getInt("AntiSpam.AntiRepeat.AllowedRepeats");
			int penaltyPoints = SCConfigManager2.config.getInt("AntiSpam.AntiRepeat.PenaltyPoints");
			boolean cancelMessage = SCConfigManager2.config.getBoolean("AntiSpam.AntiRepeat.CancelMessage");
			
			if(data.getRepeats() >= allowedRepeats && !event.isCensored()) {
				
				event.getPlayer().sendMessage(SCUtils.getMessageFromMessagesFile("AntiSpam.RepeatMessage"));
				
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
		
		long removeWarnEvery = SCConfigManager2.config.getInt("AntiSpam.AntiSpam.RemoveWarnEvery");
		
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
