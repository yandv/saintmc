package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class GladiatorKit extends DefaultKit {

	public GladiatorKit() {
		super("gladiator", "Puxe um jogador clicando com o direito nele para um luta nos ceus", new ItemStack(Material.IRON_FENCE));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("gladiator"));
	}

}
