package tk.yallandev.saintmc.skwyars.game.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Kit {
	
	String getName();
	
	ItemStack getKitIcon();
	
	void apply(Player player);

	String getDescription();
	
	int getPrice();
	
}
