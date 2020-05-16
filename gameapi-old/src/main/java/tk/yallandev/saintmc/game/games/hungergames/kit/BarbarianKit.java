package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class BarbarianKit extends DefaultKit {

	public BarbarianKit() {
		super("barbarian", "§%ability-barbarian-description%§", new ItemBuilder().type(Material.WOOD_SWORD).glow().enchantment(Enchantment.DURABILITY, 1).build(), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("barbarian"));
	}

}
