package tk.yallandev.saintmc.kitpvp.event.challenge;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import lombok.Getter;
import tk.yallandev.saintmc.kitpvp.warp.Warp;
import tk.yallandev.saintmc.kitpvp.warp.challenge.ChallengeType;

@Getter
public class FightStartEvent extends PlayerEvent {
	
	private static final HandlerList handlers = new HandlerList();
	private Player target;
	private Warp warp;
	private ChallengeType challengeType;

	public FightStartEvent(Player who, Player target, Warp warp, ChallengeType challengeType) {
		super(who);
		this.target = target;
		this.warp = warp;
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
