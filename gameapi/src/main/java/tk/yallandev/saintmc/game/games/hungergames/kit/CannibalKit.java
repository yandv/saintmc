package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class CannibalKit extends DefaultKit {

	public CannibalKit() {
		super("cannibal", "Ao bater em algum player ira deixa-lo com fome e a sua recuperará", new ItemStack(Material.RAW_FISH), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("cannibal"));
	}
}
