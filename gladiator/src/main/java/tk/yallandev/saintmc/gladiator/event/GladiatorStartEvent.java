package tk.yallandev.saintmc.gladiator.event;

import org.bukkit.event.Cancellable;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.bukkit.event.NormalEvent;
import tk.yallandev.saintmc.gladiator.challenge.Challenge;

@Getter
public class GladiatorStartEvent extends NormalEvent implements Cancellable {

	private Challenge challenge;
	@Setter
	private boolean cancelled;

	public GladiatorStartEvent(Challenge challenge) {
		this.challenge = challenge;
	}

}
