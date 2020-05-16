package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class ReaperKit extends DefaultKit {

	public ReaperKit() {
		super("reaper", "�%ability-reaper-description%�", new ItemStack(Material.WOOD_HOE), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("reaper"));
	}

}
