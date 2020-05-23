package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class IronmanKit extends DefaultKit {

	public IronmanKit() {
		super("ironman", "Receba ferros quando matar um jogador", new ItemStack(Material.IRON_INGOT));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("ironman"));
	}

}
