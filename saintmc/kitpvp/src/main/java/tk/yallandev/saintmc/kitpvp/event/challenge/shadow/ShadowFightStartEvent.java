package tk.yallandev.saintmc.kitpvp.event.challenge.shadow;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.kitpvp.event.challenge.FightStartEvent;

public class ShadowFightStartEvent extends FightStartEvent {

	public ShadowFightStartEvent(Player who, Player target) {
		super(who, target);
	}

}
