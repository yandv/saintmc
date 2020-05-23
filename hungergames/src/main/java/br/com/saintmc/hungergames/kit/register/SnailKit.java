package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class SnailKit extends DefaultKit {

	public SnailKit() {
		super("snail", "Deixe seus inimigos mais lerdos", new ItemStack(Material.WEB));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("snail"));
	}

}
