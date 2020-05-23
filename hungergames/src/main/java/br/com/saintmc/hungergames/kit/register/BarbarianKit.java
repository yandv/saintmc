package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class BarbarianKit extends DefaultKit {

	public BarbarianKit() {
		super("barbarian", "Ganhe XP matando players para evoluir sua espada", new ItemBuilder().type(Material.WOOD_SWORD).glow().enchantment(Enchantment.DURABILITY, 1).build());
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("barbarian"));
	}

}
