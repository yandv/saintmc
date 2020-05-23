package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class BarbarianKit extends DefaultKit {

	public BarbarianKit() {
		super("barbarian", "Ganhe XP matando players para evoluir sua espada", new ItemBuilder().type(Material.WOOD_SWORD).glow().enchantment(Enchantment.DURABILITY, 1).build(), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("barbarian"));
	}

}
