package tk.yallandev.saintmc.gladiator.event;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.NormalEvent;
import tk.yallandev.saintmc.gladiator.challenge.Challenge;

@Getter
@AllArgsConstructor
public class GladiatorSpectatorEvent extends NormalEvent {
	
	private Player player;
	private Challenge challenge;
	private Action action;
	
	public enum Action {
		
		JOIN, LEAVE
		
	}

}
