package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class GrapplerKit extends DefaultKit {

	public GrapplerKit() {
		super("grappler", "Movimente-se mais rapido com sua corda", new ItemStack(Material.LEASH));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("grappler"));
	}

}
