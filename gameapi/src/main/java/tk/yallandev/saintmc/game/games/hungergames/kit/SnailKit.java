package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class SnailKit extends DefaultKit {

	public SnailKit() {
		super("snail", "Deixe seus inimigos mais lerdos", new ItemStack(Material.WEB), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("snail"));
	}

}
