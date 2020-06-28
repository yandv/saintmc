package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class ForgerKit extends DefaultKit {

	public ForgerKit() {
		super("forger", "Forje barras misturando carvão e minérios em seu inventário", new ItemStack(Material.COAL),
				28500);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("forger"));
	}

}
