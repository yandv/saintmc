package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class SupernovaKit extends DefaultKit {

	public SupernovaKit() {
		super("supernova", "§%ability-supernova-description%§", new ItemStack(Material.NETHER_STAR), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("supernova"));
	}

}
