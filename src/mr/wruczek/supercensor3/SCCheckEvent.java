package mr.wruczek.supercensor3;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0
 * International License. http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public final class SCCheckEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Player player;
	private String message;
	private String originalMessage;
	private boolean censored;

	public SCCheckEvent(Player player, String message) {
		this.player = player;
		this.message = message;
		this.originalMessage = message;
	}

	public String getMessage() {
		return message;
	}

	public String getOriginalMessage() {
		return originalMessage;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isCensored() {
		return censored;
	}

	public void setCensored(boolean censored) {
		this.censored = censored;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Player getPlayer() {
		return player;
	}
}