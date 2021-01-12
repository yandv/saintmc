package tk.yallandev.saintmc.gladiator.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.NormalEvent;
import tk.yallandev.saintmc.gladiator.challenge.Challenge;

@Getter
@AllArgsConstructor
public class GladiatorPulseEvent extends NormalEvent {
	
	private Challenge challenge;

}
