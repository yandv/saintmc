package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class HotpotatoKit extends DefaultKit {

	public HotpotatoKit() {
		super("hotpotato", "§%ability-hotpotato-description%§", new ItemStack(Material.TNT), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("hotpotato"));
	}

}
