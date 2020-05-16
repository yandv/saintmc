package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class SwitcherKit extends DefaultKit {

	public SwitcherKit() {
		super("switcher", "§%ability-switcher-description%§", new ItemStack(Material.SNOW_BALL), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("switcher"));
	}

}
