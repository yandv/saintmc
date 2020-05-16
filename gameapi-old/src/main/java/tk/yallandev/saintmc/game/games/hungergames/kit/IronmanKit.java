package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class IronmanKit extends DefaultKit {

	public IronmanKit() {
		super("ironman", "§%ability-ironman-description%§", new ItemStack(Material.IRON_INGOT), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("ironman"));
	}

}
