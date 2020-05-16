package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class TimelordKit extends DefaultKit {

	public TimelordKit() {
		super("timelord", "�%ability-timelord-description%�", new ItemStack(Material.WATCH), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("timelord"));
	}

}
