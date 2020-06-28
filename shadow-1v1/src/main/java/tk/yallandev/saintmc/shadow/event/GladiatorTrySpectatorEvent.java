package tk.yallandev.saintmc.shadow.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;
import tk.yallandev.saintmc.shadow.challenge.Challenge;

@Getter
public class GladiatorTrySpectatorEvent extends PlayerCancellableEvent {

	private Challenge challenge;

	public GladiatorTrySpectatorEvent(Player player, Challenge challenge) {
		super(player);
		this.challenge = challenge;
	}

}
