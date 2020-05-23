package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class UrgalKit extends DefaultKit {

	public UrgalKit() {
		super("urgal", "Fique mais forte tomando uma pocao de força", new ItemStack(Material.POTION, 1, (short) 8201), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("urgal"));
	}

}
