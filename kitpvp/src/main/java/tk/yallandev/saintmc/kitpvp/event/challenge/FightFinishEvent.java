package tk.yallandev.saintmc.kitpvp.event.challenge;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import lombok.Getter;
import tk.yallandev.saintmc.kitpvp.warp.Warp;

@Getter
public class FightFinishEvent extends PlayerEvent {
	
	private static final HandlerList handlers = new HandlerList();
	private Player target;
	private Player winner;
	private Warp warp;

	public FightFinishEvent(Player who, Player target, Player winner, Warp warp) {
		super(who);
		this.target = target;
		this.winner = winner;
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