package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class ReaperKit extends DefaultKit {

	public ReaperKit() {
		super("reaper", "Use sua enxada para deixar seu inimigo com wither", new ItemStack(Material.WOOD_HOE), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("reaper"));
	}

}
