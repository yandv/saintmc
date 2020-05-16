package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class NinjaKit extends DefaultKit {

	public NinjaKit() {
		super("ninja", "§%ability-ninja-description%§", new ItemStack(Material.EMERALD), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("ninja"));
	}

}
