package tk.yallandev.saintmc.bukkit.utils.item;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class ItemUtils {

	public static void dropAndClear(Player p, List<ItemStack> items, Location l) {
		dropItems(items, l);
		p.closeInventory();
		p.getInventory().setArmorContents(new ItemStack[4]);
		p.getInventory().clear();
		p.setItemOnCursor(null);
		for (PotionEffect pot : p.getActivePotionEffects()) {
			p.removePotionEffect(pot.getType());
			break;
		}
	}

	public static void dropItems(List<ItemStack> items, Location l) {
		World world = l.getWorld();
		for (ItemStack item : items) {
			if (item == null || item.getType() == Material.AIR)
				continue;
			if (item.hasItemMeta())
				world.dropItemNaturally(l, item.clone()).getItemStack().setItemMeta(item.getItemMeta());
			else
				world.dropItemNaturally(l, item);
		}
	}

}
