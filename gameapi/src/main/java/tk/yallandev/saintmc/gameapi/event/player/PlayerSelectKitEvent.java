package tk.yallandev.saintmc.gameapi.event.player;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;

public class PlayerSelectKitEvent extends PlayerCancellableEvent {

//	private Kit kit;

	public PlayerSelectKitEvent(Player player/*, Kit kit*/) {
		super(player);
		
//		this.kit = kit;
	}

//	public Kit getKit() {
//		return kit;
//	}

}
