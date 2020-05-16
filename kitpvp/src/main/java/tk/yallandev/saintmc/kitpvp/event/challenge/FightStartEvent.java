package tk.yallandev.saintmc.kitpvp.event.challenge;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import lombok.Getter;

@Getter
public class FightStartEvent extends PlayerEvent {
	
	private static final HandlerList handlers = new HandlerList();
	private Player target;

	public FightStartEvent(Player who, Player target) {
		super(who);
		this.target = target;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
