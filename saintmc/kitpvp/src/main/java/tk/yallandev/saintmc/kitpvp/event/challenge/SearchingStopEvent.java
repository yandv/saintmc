package tk.yallandev.saintmc.kitpvp.event.challenge;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;

@Getter
public class SearchingStopEvent extends PlayerCancellableEvent {
	
	public SearchingStopEvent(Player player) {
		super(player);
	}

}
