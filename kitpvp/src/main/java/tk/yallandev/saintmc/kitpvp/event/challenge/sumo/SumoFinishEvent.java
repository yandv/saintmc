package tk.yallandev.saintmc.kitpvp.event.challenge.sumo;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.kitpvp.event.challenge.FightFinishEvent;
import tk.yallandev.saintmc.kitpvp.warp.Warp;

public class SumoFinishEvent extends FightFinishEvent {

	public SumoFinishEvent(Player who, Player target, Player winner, Warp warp) {
		super(who, target, winner, warp);
	}

}
