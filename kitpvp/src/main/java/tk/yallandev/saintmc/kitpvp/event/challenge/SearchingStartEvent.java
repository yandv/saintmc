package tk.yallandev.saintmc.kitpvp.event.challenge;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;

@Getter
public class SearchingStartEvent extends PlayerCancellableEvent  {
	
	public SearchingStartEvent(Player player) {
		super(player);
	}

}
