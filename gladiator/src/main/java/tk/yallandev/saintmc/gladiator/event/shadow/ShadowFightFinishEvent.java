package tk.yallandev.saintmc.gladiator.event.shadow;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.gladiator.event.FightFinishEvent;

public class ShadowFightFinishEvent extends FightFinishEvent {

	public ShadowFightFinishEvent(Player who, Player target, Player winner) {
		super(who, target, winner);
	}

}
