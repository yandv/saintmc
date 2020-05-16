package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class VikingKit extends DefaultKit {

	public VikingKit() {
		super("viking", "§%ability-viking-description%§", new ItemStack(Material.DIAMOND_AXE), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("viking"));
	}

}
