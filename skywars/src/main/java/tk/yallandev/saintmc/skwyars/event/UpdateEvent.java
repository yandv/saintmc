package tk.yallandev.saintmc.skwyars.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UpdateEvent extends Event {

	public static final HandlerList handlers = new HandlerList();
	private UpdateType type;
	private long currentTick;

	public UpdateEvent(UpdateType type) {
		this(type, -1);
	}
	
	public UpdateEvent(UpdateType type, long currentTick) {
		this.type = type;
		this.currentTick = currentTick;
	}

	public UpdateType getType() {
		return type;
	}

	public long getCurrentTick() {
		return currentTick;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public static enum UpdateType {
		TICK, SECOND, MINUTE;
	}	
	
	public static class UpdateScheduler implements Runnable {

	    private long currentTick;

	    @Override
	    public void run() {
	        currentTick++;
	        Bukkit.getPluginManager().callEvent(new UpdateEvent(UpdateType.TICK, currentTick));

	        if (currentTick % 20 == 0) {
	            Bukkit.getPluginManager().callEvent(new UpdateEvent(UpdateType.SECOND, currentTick));
	        }

	        if (currentTick % 1200 == 0) {
	            Bukkit.getPluginManager().callEvent(new UpdateEvent(UpdateType.MINUTE, currentTick));
	        }
	    }
	}
	
}
