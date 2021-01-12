package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class CamelKit extends DefaultKit {

	public CamelKit() {
		super("camel", "Receba efeitos em certos biomas e faça sopas especiais.", new ItemStack(Material.SAND), 18000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("camel"));
	}

}