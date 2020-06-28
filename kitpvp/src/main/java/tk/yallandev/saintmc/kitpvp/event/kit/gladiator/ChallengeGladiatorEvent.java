package tk.yallandev.saintmc.kitpvp.event.kit.gladiator;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;

@Getter
public class ChallengeGladiatorEvent extends PlayerCancellableEvent {
	
	private Player target;

	public ChallengeGladiatorEvent(Player player, Player target) {
		super(player);
		this.target = target;
	}

}
