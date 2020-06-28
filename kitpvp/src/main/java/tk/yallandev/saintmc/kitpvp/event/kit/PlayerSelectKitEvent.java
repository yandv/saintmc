package tk.yallandev.saintmc.kitpvp.event.kit;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

@Getter
public class PlayerSelectKitEvent extends PlayerCancellableEvent {

	private Kit kit;
	
	public PlayerSelectKitEvent(Player player, Kit kit) {
		super(player);
		this.kit = kit;
	}

}
