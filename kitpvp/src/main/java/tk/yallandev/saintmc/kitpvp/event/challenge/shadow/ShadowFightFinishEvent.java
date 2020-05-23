package tk.yallandev.saintmc.kitpvp.event.challenge.shadow;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.kitpvp.event.challenge.FightFinishEvent;
import tk.yallandev.saintmc.kitpvp.warp.Warp;

public class ShadowFightFinishEvent extends FightFinishEvent {

	public ShadowFightFinishEvent(Player who, Player target, Player winner, Warp warp) {
		super(who, target, winner, warp);
	}

}
