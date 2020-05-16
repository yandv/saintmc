package tk.yallandev.saintmc.kitpvp.event.challenge.sumo;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.kitpvp.event.challenge.FightStartEvent;

public class SumoStartEvent extends FightStartEvent {

	public SumoStartEvent(Player who, Player target) {
		super(who, target);
	}

}
