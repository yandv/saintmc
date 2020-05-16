package tk.yallandev.saintmc.kitpvp.event.warp;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import lombok.Getter;
import tk.yallandev.saintmc.kitpvp.warp.Warp;

@Getter
public class PlayerWarpQuitEvent extends PlayerEvent {
	
	private static final HandlerList handlers = new HandlerList();
	
	private Warp warp;

	public PlayerWarpQuitEvent(Player player, Warp warp) {
		super(player);
		
		this.warp = warp;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
