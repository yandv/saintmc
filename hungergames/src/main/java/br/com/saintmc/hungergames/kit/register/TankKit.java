package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class TankKit extends DefaultKit {

	public TankKit() {
		super("tank", "Seus inimigos explodirao quando você matar eles", new ItemStack(Material.TNT));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("tank"));
	}

}
