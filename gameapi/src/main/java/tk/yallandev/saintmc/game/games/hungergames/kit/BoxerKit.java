package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class BoxerKit extends DefaultKit {

	public BoxerKit() {
		super("boxer", "Leve menos dano e dê mais dano", new ItemStack(Material.STONE_SWORD), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("boxer"));
		setOption("boxer", "DAMAGE", 2);
		setOption("boxer", "REDUCE", 1);
	}

}
