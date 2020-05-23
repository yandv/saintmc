package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class ViperKit extends DefaultKit {

	public ViperKit() {
		super("viper", "Deixe seus inimigos envenenados", new ItemStack(Material.SPIDER_EYE), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("viper"));
	}

}
