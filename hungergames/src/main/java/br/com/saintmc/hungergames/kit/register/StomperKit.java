package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class StomperKit extends DefaultKit {

	public StomperKit() {
		super("stomper", "Esmague seus inimigos", new ItemStack(Material.IRON_BOOTS));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("stomper"));
	}

}
