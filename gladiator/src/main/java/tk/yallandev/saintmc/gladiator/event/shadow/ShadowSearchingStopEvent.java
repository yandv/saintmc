package tk.yallandev.saintmc.gladiator.event.shadow;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.gladiator.event.SearchingStopEvent;

public class ShadowSearchingStopEvent extends SearchingStopEvent {

	public ShadowSearchingStopEvent(Player player) {
		super(player);
	}

}
