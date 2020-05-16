package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class WormKit extends DefaultKit {

	public WormKit() {
		super("worm", "§%ability-worm-description%§", new ItemStack(Material.DIRT), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("worm"));
	}

}
