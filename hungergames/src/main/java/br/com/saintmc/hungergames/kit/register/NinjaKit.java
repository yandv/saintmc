package br.com.saintmc.hungergames.kit.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class NinjaKit extends DefaultKit {

	public NinjaKit() {
		super("ninja", "Aperte SHIFT para teletransportar-se para o ultimo jogador hitado",
				new ItemStack(Material.NETHER_STAR), 34000,
				Arrays.asList(AjninKit.class, StomperKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("ninja"));
	}

}
