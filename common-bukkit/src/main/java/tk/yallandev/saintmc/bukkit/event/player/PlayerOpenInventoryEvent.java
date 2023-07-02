package tk.yallandev.saintmc.bukkit.event.player;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.NormalEvent;

@Getter
@AllArgsConstructor
public class PlayerOpenInventoryEvent extends NormalEvent {

    private Player player;
	private Inventory inventory;

}
