package br.com.saintmc.hungergames.event.kit;

import org.bukkit.entity.Player;

import br.com.saintmc.hungergames.kit.Kit;
import br.com.saintmc.hungergames.kit.KitType;
import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;

@Getter
public class PlayerSelectKitEvent extends PlayerCancellableEvent {
	
	private Kit kit;
	private KitType kitType;
	
	public PlayerSelectKitEvent(Player player, Kit kit, KitType kitType) {
		super(player);
		this.kit = kit;
		this.kitType = kitType;
	}

}
