package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class KangarooKit extends DefaultKit {

	public KangarooKit() {
		super("kangaroo", "Movimente-se mais rapido com seu kangaroo", new ItemStack(Material.FIREWORK), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("kangaroo"));
		setOption("kangaroo", "COOLDOWN", 5);
	}

}
