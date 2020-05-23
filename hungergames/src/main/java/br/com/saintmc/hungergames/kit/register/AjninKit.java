package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class AjninKit extends DefaultKit {

	public AjninKit() {
		super("ajnin", "Aperte SHIFT para teletransportar o ultimo jogador hitado até você", new ItemStack(Material.NETHER_STAR));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("ajnin"));
	}

}
