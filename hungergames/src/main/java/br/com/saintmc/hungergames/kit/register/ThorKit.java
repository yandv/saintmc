package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class ThorKit extends DefaultKit {

	public ThorKit() {
		super("thor", "Lance raios com o seu machado", new ItemStack(Material.WOOD_AXE));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("thor"));
	}

}
