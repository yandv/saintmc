package tk.yallandev.saintmc.kitpvp.event.warp;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import lombok.Getter;
import tk.yallandev.saintmc.kitpvp.warp.Warp;

@Getter
public class PlayerWarpDeathEvent extends PlayerEvent {

	private static final HandlerList handlers = new HandlerList();

	private Player killer;
	private Warp warp;

	public PlayerWarpDeathEvent(Player player, Player killer, Warp warp) {
		super(player);
		this.killer = killer;
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
