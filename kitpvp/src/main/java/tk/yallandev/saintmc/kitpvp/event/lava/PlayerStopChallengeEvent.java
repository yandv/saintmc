package tk.yallandev.saintmc.kitpvp.event.lava;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tk.yallandev.saintmc.bukkit.event.NormalEvent;
import tk.yallandev.saintmc.kitpvp.warp.lava.ChallengeInfo;
import tk.yallandev.saintmc.kitpvp.warp.lava.ChallengeStage;

@RequiredArgsConstructor
@Getter
public class PlayerStopChallengeEvent extends NormalEvent {

	private final Player player;
	private final ChallengeStage challengeType;
	private final ChallengeInfo challengeInfo;

	private boolean death = false;

	public PlayerStopChallengeEvent death() {
		this.death = !death;
		return this;
	}

}
