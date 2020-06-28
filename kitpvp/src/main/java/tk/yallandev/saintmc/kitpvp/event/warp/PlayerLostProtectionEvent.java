package tk.yallandev.saintmc.kitpvp.event.warp;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;
import tk.yallandev.saintmc.kitpvp.warp.Warp;

public class PlayerLostProtectionEvent extends PlayerCancellableEvent {

	private Warp warp;

	public PlayerLostProtectionEvent(Player player, Warp warp) {
		super(player);
		this.warp = warp;
	}
	
	public Warp getWarp() {
		return warp;
	}

}
