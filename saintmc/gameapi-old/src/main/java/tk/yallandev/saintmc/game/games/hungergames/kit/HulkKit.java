package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class HulkKit extends DefaultKit {

	public HulkKit() {
		super("hulk", "§%ability-hulk-description%§", new ItemStack(Material.DISPENSER), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("hulk"));
	}

}
