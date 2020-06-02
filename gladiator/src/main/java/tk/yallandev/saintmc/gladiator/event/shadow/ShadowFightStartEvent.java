package tk.yallandev.saintmc.gladiator.event.shadow;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.gladiator.challenge.ChallengeType;
import tk.yallandev.saintmc.gladiator.event.FightStartEvent;

public class ShadowFightStartEvent extends FightStartEvent {
	
	public ShadowFightStartEvent(Player who, Player target, ChallengeType challengeType) {
		super(who, target, challengeType);
	}

}
