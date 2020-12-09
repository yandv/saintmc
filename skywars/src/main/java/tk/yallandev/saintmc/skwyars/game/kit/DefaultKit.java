package tk.yallandev.saintmc.skwyars.game.kit;

import org.bukkit.inventory.ItemStack;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class DefaultKit implements Kit {
	
	private String name;
	private ItemStack kitIcon;
	private String description;

}
