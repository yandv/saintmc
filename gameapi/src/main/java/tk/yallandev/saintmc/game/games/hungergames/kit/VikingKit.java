package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class VikingKit extends DefaultKit {

	public VikingKit() {
		super("viking", "Dê mais dano com machados", new ItemStack(Material.DIAMOND_AXE), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("viking"));
	}

}
