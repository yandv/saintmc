package tk.yallandev.saintmc.gameapi.event;

import org.bukkit.event.HandlerList;

public class Event extends org.bukkit.event.Event {
	public static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
