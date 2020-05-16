package tk.yallandev.saintmc.game.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.Ability;

public class SearchUtils {

	public static List<Ability> searchAbilities(String searchName, List<Ability> abilityList) {
		List<Ability> list = new ArrayList<>();
		for (Ability ability : abilityList) {
			if (ability.getName().toLowerCase().contains(searchName.toLowerCase()))
				list.add(ability);
		}
		return list;
	}

	public static List<ItemStack> searchItemStacks(String searchName, List<ItemStack> itemList) {
		List<ItemStack> list = new ArrayList<>();
		for (ItemStack item : itemList) {
			if (item.getType().toString().toLowerCase().contains(searchName.toLowerCase()))
				list.add(item);
		}
		return list;
	}
}
