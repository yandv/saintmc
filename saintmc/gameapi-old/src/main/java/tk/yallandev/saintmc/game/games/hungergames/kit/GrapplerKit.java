package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class GrapplerKit extends DefaultKit {

	public GrapplerKit() {
		super("grappler", "§%ability-grappler-description%§", new ItemStack(Material.LEASH), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("grappler"));
		setOption("grappler", "COOLDOWN", 5);
	}

}
