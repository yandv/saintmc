package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class NinjaKit extends DefaultKit {

	public NinjaKit() {
		super("ninja", "Aperte SHIFT para teletransportar-se para o ultimo jogador hitado", new ItemStack(Material.NETHER_STAR));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("ninja"));
	}

}
