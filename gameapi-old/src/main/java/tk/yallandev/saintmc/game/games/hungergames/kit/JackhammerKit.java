package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class JackhammerKit extends DefaultKit {

	public JackhammerKit() {
		super("jackhammer", "§%ability-jackhammer-description%§", new ItemStack(Material.STONE_AXE), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("jackhammer"));
	}

}
