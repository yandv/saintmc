package tk.yallandev.saintmc.kitpvp.event.challenge.shadow;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.kitpvp.event.challenge.FightStartEvent;
import tk.yallandev.saintmc.kitpvp.warp.Warp;
import tk.yallandev.saintmc.kitpvp.warp.challenge.ChallengeType;

public class ShadowFightStartEvent extends FightStartEvent {
	
	public ShadowFightStartEvent(Player who, Player target, Warp warp, ChallengeType challengeType) {
		super(who, target, warp, challengeType);
	}

}
