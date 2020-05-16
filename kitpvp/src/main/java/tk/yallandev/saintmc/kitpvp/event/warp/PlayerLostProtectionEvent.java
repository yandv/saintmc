package tk.yallandev.saintmc.kitpvp.event.warp;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import tk.yallandev.saintmc.kitpvp.warp.Warp;

public class PlayerLostProtectionEvent extends PlayerEvent {

	private static final HandlerList handlers = new HandlerList();
	
	private Warp warp;

	public PlayerLostProtectionEvent(Player player, Warp warp) {
		super(player);
		this.warp = warp;
	}
	
	public Warp getWarp() {
		return warp;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
