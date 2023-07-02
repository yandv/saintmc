package tk.yallandev.saintmc.shadow.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import tk.yallandev.saintmc.bukkit.event.NormalEvent;
import tk.yallandev.saintmc.shadow.challenge.Challenge;

@Getter
public class GladiatorStartEvent extends NormalEvent implements Cancellable {

	private Challenge challenge;
	@Setter
	private boolean cancelled;

	public GladiatorStartEvent(Challenge challenge) {
		this.challenge = challenge;
	}

}
