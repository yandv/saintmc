package tk.yallandev.saintmc.kitpvp.event.challenge.sumo;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.kitpvp.event.challenge.FightFinishEvent;

public class SumoFinishEvent extends FightFinishEvent {

	public SumoFinishEvent(Player who, Player target, Player winner) {
		super(who, target, winner);
	}

}
