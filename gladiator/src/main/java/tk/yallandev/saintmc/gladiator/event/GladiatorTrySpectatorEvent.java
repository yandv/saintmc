package tk.yallandev.saintmc.gladiator.event;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;
import tk.yallandev.saintmc.gladiator.challenge.Challenge;

@Getter
public class GladiatorTrySpectatorEvent extends PlayerCancellableEvent {

	private Challenge challenge;

	public GladiatorTrySpectatorEvent(Player player, Challenge challenge) {
		super(player);
		this.challenge = challenge;
	}

}
