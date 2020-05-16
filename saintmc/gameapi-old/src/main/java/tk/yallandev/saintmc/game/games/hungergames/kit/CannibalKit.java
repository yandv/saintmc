package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class CannibalKit extends DefaultKit {

	public CannibalKit() {
		super("cannibal", "§%ability-cannibal-description%§", new ItemStack(Material.RAW_FISH), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("cannibal"));
	}
}
