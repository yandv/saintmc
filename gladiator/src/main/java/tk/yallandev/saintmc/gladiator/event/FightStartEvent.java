package tk.yallandev.saintmc.gladiator.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import lombok.Getter;
import tk.yallandev.saintmc.gladiator.challenge.ChallengeType; 

@Getter
public class FightStartEvent extends PlayerEvent {
	
	private static final HandlerList handlers = new HandlerList();
	private Player target;
	private ChallengeType challengeType;

	public FightStartEvent(Player who, Player target, ChallengeType challengeType) {
		super(who);
		this.target = target;
		this.challengeType = challengeType;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
