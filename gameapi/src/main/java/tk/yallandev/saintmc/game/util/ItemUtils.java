package tk.yallandev.saintmc.game.util;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.constructor.Kit;

public class ItemUtils {

	public static void dropAndClear(Player p, List<ItemStack> items, Location l) {
		for (Kit kit : Gamer.getGamer(p).getKit()) {

			if (kit != null) {
				Iterator<ItemStack> iterator = items.iterator();
				while (iterator.hasNext()) {
					ItemStack item = iterator.next();
					for (Ability ability : kit.getAbilities()) {
						if (ability.isAbilityItem(kit, item)) {
							iterator.remove();
							break;
						}
					}
				}
			}
		}

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
