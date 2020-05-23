package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class BoxerKit extends DefaultKit {

	public BoxerKit() {
		super("boxer", "Leve menos dano e dê mais dano", new ItemStack(Material.STONE_SWORD));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("achilles"));
	}

}
