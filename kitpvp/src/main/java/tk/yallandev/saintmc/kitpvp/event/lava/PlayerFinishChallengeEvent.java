package tk.yallandev.saintmc.kitpvp.event.lava;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.kitpvp.warp.lava.ChallengeInfo;
import tk.yallandev.saintmc.kitpvp.warp.lava.ChallengeStage;

public class PlayerFinishChallengeEvent extends PlayerStopChallengeEvent {

	public PlayerFinishChallengeEvent(Player player, ChallengeStage challengeType, ChallengeInfo challengeInfo) {
		super(player, challengeType, challengeInfo);
	}
	
}
