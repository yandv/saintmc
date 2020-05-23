package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class TankKit extends DefaultKit {

	public TankKit() {
		super("tank", "Seus inimigos explodirao quando você matar eles", new ItemStack(Material.TNT), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("tank"));
	}

}
