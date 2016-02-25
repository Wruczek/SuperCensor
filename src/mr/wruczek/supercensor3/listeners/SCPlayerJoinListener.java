package mr.wruczek.supercensor3.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import mr.wruczek.supercensor3.SCMain;
import mr.wruczek.supercensor3.commands.subcommands.SCMuteChatManager;
import mr.wruczek.supercensor3.commands.subcommands.SCSelfMuteManager;
import mr.wruczek.supercensor3.utils.SCUpdater;
import mr.wruczek.supercensor3.utils.SCUtils;
import net.gravitydevelopment.updater.GravityUpdater.UpdateResult;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SCPlayerJoinListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		Bukkit.getScheduler().runTaskAsynchronously(SCMain.getInstance(), new Runnable() {
			@Override
			public void run() {
				checkForUpdates(event);
			}
		});
		
		changeJoinMessage(event);
		
		// We wait to send it after Essentials MOTD ect.
		Bukkit.getScheduler().scheduleSyncDelayedTask(SCMain.getInstance(), new Runnable() {
			@Override
			public void run() {
				remind(event);
				
				String uuid = event.getPlayer().getUniqueId().toString();
				if (uuid.equals("16cb51e4-4c17-41a2-9855-c8f343764fcf") // online-mode=true
						|| uuid.equals("7a41ee58-57ea-324a-9bdc-228ce5f0458c")) { // online-mode=false
					event.getPlayer().sendMessage(SCUtils.getPluginPrefix() +
							SCUtils.color("&6Hello Wruczek! This server is using SuperCensor in version "
									+ SCMain.getInstance().getDescription().getVersion()));
				}
			}
		});
		
		// playerData(event);
	}
	
	private void remind(PlayerJoinEvent event) {
		if(SCSelfMuteManager.isSelfMuted(event.getPlayer())) {
			event.getPlayer().sendMessage(SCUtils.getMessageFromMessagesFile("Commands.SelfMute.JoinReminder"));
		}
		
		if(SCMuteChatManager.isChatMuted()) {
			if(SCMuteChatManager.isReasonSet())
				event.getPlayer().sendMessage(SCUtils.getMessageFromMessagesFile("Commands.MuteChat.JoinReminderReason")
						.replace("%reason%", SCMuteChatManager.getReason()));
			else
				event.getPlayer().sendMessage(SCUtils.getMessageFromMessagesFile("Commands.MuteChat.JoinReminder"));
		}
	}

	private void checkForUpdates(PlayerJoinEvent event) {
		if (!SCUtils.getBooleanFromConfig("AutoUpdater.CheckOnJoin")
				|| !event.getPlayer().hasPermission("supercensor")) {
			return;
		}
		
		UpdateResult result = SCUpdater.instance.checkForUpdates();
		
		if(result == UpdateResult.SUCCESS) {
			event.getPlayer().sendMessage(
					SCUtils.formatUpdaterMessage(SCUtils.getMessageFromMessagesFile("Updater.ToPlayer.Success"))
			);
		} else if(result == UpdateResult.UPDATE_AVAILABLE) {
			event.getPlayer().sendMessage(
					SCUtils.formatUpdaterMessage(SCUtils.getMessageFromMessagesFile("Updater.ToPlayer.UpdateAvailable"))
			);
		}
	}
	
	private void changeJoinMessage(PlayerJoinEvent event) {
		if(SCUtils.getBooleanFromConfig("FunStuff.JoinMessage.Change")) {
			String message = SCUtils.getStringFromConfig("FunStuff.JoinMessage.Message");
			
			if(message != null)
				message = message.replace("%nick%", event.getPlayer().getDisplayName());
			
			event.setJoinMessage(message);
		}
	}
}