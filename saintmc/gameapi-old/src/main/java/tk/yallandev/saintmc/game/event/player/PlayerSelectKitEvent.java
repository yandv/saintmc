package tk.yallandev.saintmc.game.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import tk.yallandev.saintmc.game.constructor.Kit;
import tk.yallandev.saintmc.game.event.Event;

public class PlayerSelectKitEvent extends Event implements Cancellable {

	private boolean cancelled;
	private Player player;
	private Kit kit;

	public PlayerSelectKitEvent(Player player, Kit kit) {
		this.player = player;
		this.kit = kit;
	}

	public Player getPlayer() {
		return player;
	}

	public Kit getKit() {
		return kit;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		this.cancelled = arg0;
	}

}
