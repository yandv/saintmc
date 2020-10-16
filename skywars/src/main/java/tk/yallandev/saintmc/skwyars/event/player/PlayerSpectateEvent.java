package tk.yallandev.saintmc.skwyars.event.player;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.skwyars.event.PlayerCancellableEvent;

public class PlayerSpectateEvent extends PlayerCancellableEvent {

	public PlayerSpectateEvent(Player player) {
		super(player);
	}

}
