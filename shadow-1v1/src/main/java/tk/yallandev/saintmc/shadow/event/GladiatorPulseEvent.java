package tk.yallandev.saintmc.shadow.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.NormalEvent;
import tk.yallandev.saintmc.shadow.challenge.Challenge;

@Getter
@AllArgsConstructor
public class GladiatorPulseEvent extends NormalEvent {
	
	private Challenge challenge;

}
