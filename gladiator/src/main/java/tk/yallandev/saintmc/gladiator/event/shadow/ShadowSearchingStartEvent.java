package tk.yallandev.saintmc.gladiator.event.shadow;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.gladiator.event.SearchingStartEvent;

public class ShadowSearchingStartEvent extends SearchingStartEvent {

	public ShadowSearchingStartEvent(Player player) {
		super(player);
	}

}
