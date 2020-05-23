package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class AnchorKit extends DefaultKit {

	public AnchorKit() {
		super("anchor", "Se prenda ao chão e não saia dele", new ItemStack(Material.ANVIL));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("anchor"));
	}

}
