package tk.yallandev.saintmc.skwyars.game.kit;

import org.bukkit.inventory.ItemStack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public abstract class DefaultKit implements Kit {

	private final String name;
	private final ItemStack kitIcon;
	private final String description;

	private int price = 15000;

}
