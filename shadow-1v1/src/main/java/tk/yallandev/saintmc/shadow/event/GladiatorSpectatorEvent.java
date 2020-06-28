package tk.yallandev.saintmc.shadow.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import tk.yallandev.saintmc.bukkit.event.NormalEvent;
import tk.yallandev.saintmc.shadow.challenge.Challenge;

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
