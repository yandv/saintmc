package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class GrapplerKit extends DefaultKit {

	public GrapplerKit() {
		super("grappler", "Movimente-se mais rapido com sua corda", new ItemStack(Material.LEASH), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("grappler"));
		setOption("grappler", "COOLDOWN", 5);
	}

}
