package tk.yallandev.saintmc.bukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;

@Getter
public class MenuOpenEvent extends PlayerCancellableEvent {
	
	private Inventory inventory;

	public MenuOpenEvent(Player player, Inventory inventory) {
		super(player);
		this.inventory = inventory;
	}

}
