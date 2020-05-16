package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class ThorKit extends DefaultKit {

	public ThorKit() {
		super("thor", "§%ability-thor-description%§", new ItemStack(Material.WOOD_AXE), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("thor"));
	}

}
