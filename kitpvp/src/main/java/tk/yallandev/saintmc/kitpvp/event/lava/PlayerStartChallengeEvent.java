package tk.yallandev.saintmc.kitpvp.event.lava;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;
import tk.yallandev.saintmc.kitpvp.warp.lava.ChallengeStage;

@Getter
public class PlayerStartChallengeEvent extends PlayerCancellableEvent {

	private ChallengeStage challengeType;

	public PlayerStartChallengeEvent(Player player, ChallengeStage challengeType) {
		super(player);
		this.challengeType = challengeType;
	}

}
