package tk.yallandev.saintmc.kitpvp.event.challenge.shadow;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.kitpvp.event.challenge.FightFinishEvent;

public class ShadowFightFinishEvent extends FightFinishEvent {

	public ShadowFightFinishEvent(Player who, Player target, Player winner) {
		super(who, target, winner);
	}

}
