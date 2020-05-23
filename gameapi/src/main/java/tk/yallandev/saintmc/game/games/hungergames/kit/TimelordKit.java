package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class TimelordKit extends DefaultKit {

	public TimelordKit() {
		super("timelord", "Pare o tempo com seu relógio", new ItemStack(Material.WATCH), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("timelord"));
	}

}
