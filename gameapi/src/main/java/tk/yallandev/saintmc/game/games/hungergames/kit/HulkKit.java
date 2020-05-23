package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class HulkKit extends DefaultKit {

	public HulkKit() {
		super("hulk", "Pegue e esmague seus inimigos", new ItemStack(Material.DISPENSER), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("hulk"));
	}

}
