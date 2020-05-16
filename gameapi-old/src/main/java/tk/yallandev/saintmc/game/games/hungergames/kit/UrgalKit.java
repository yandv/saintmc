package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class UrgalKit extends DefaultKit {

	public UrgalKit() {
		super("urgal", "§%ability-urgal-description%§", new ItemStack(Material.POTION, 1, (short) 8201), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("urgal"));
	}

}
