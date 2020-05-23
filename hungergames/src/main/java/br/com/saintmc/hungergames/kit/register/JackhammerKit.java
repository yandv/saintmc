package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class JackhammerKit extends DefaultKit {

	public JackhammerKit() {
		super("jackhammer", "Receba ferros quando matar um jogador", new ItemStack(Material.STONE_AXE));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("jackhammer"));
	}

}
