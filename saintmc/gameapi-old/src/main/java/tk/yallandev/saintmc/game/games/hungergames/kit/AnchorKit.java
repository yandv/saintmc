package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class AnchorKit extends DefaultKit {

	public AnchorKit() {
		super("anchor", "§%ability-anchor-description%§", new ItemStack(Material.ANVIL), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("anchor"));
	}

}
