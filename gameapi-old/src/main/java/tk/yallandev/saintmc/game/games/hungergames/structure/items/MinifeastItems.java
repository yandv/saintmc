package tk.yallandev.saintmc.game.games.hungergames.structure.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MinifeastItems implements Items {
	private static Random r = new Random();

	@Override
	public List<ItemStack> generateItems() {
		List<ItemStack> feastItems = new ArrayList<>();
		feastItems.addAll(addItem(Material.IRON_SWORD, 1, 2));
		feastItems.addAll(addItem(Material.DIAMOND, 2, 5));
		feastItems.addAll(addItem(Material.IRON_INGOT, 4, 8));
		feastItems.addAll(addItem(Material.BREAD, 10, 20));
		feastItems.addAll(addItem(Material.INK_SACK, (short) 3, 6, 10));
		feastItems.addAll(addItem(Material.MUSHROOM_SOUP, 6, 10));
		feastItems.addAll(addItem(Material.WEB, 4, 8));
		feastItems.addAll(addItem(Material.TNT, 4, 10));
		feastItems.addAll(addItem(Material.FLINT_AND_STEEL, 2, 4));
		feastItems.addAll(addItem(Material.EXP_BOTTLE, 12, 24));
		feastItems.addAll(addItem(Material.INK_SACK, (short) 4, 12, 24));
		feastItems.addAll(addItem(Material.POTION, (short) 16386, 0, 1));
		feastItems.addAll(addItem(Material.POTION, (short) 16388, 0, 1));
		feastItems.addAll(addItem(Material.POTION, (short) 16394, 0, 1));
		feastItems.addAll(addItem(Material.POTION, (short) 16396, 0, 1));
		Collections.shuffle(feastItems);
		return feastItems;
	}

	private List<ItemStack> addItem(Material mat, int min, int max) {
		return addItem(mat, (short) 0, min, max);
	}

	private List<ItemStack> addItem(Material mat, short durability, int min, int max) {
		List<ItemStack> items = new ArrayList<>();
		for (int i = 0; i <= min + r.nextInt(max - min); i++) {
			items.add(new ItemStack(mat, 1, durability));
		}
		return items;
	}
}
