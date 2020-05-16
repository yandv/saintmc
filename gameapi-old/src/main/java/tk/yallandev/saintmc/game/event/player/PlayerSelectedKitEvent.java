package tk.yallandev.saintmc.game.event.player;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.game.constructor.Kit;
import tk.yallandev.saintmc.game.event.Event;

public class PlayerSelectedKitEvent extends Event {

	private Player player;
	private Kit kit;

	public PlayerSelectedKitEvent(Player player, Kit kit) {
		this.player = player;
		this.kit = kit;
	}

	public Player getPlayer() {
		return player;
	}

	public Kit getKit() {
		return kit;
	}

}
