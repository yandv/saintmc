package tk.yallandev.saintmc.game.games.hungergames.util;

import org.bukkit.inventory.ItemStack;

public class ItemUtils {

	public static boolean isEquals(ItemStack item, ItemStack it) {
		if (it.getType() == item.getType()) {
			if (it.hasItemMeta() && item.hasItemMeta()) {
				if (it.getItemMeta().hasDisplayName() && item.getItemMeta().hasDisplayName()) {
					if (item.getItemMeta().getDisplayName().equals(it.getItemMeta().getDisplayName()))
						return true;
				} else if (!it.getItemMeta().hasDisplayName() && !item.getItemMeta().hasDisplayName())
					return true;
			} else if (!it.hasItemMeta() && !item.hasItemMeta())
				return true;
		}
		
		return false;
	}
}
