package tk.yallandev.saintmc.gladiator.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import lombok.Getter;

@Getter
public class FightFinishEvent extends PlayerEvent {
	
	private static final HandlerList handlers = new HandlerList();
	private Player target;
	private Player winner;

	public FightFinishEvent(Player who, Player target, Player winner) {
		super(who);
		this.target = target;
		this.winner = winner;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}