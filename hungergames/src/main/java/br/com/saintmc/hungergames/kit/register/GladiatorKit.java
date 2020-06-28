package br.com.saintmc.hungergames.kit.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class GladiatorKit extends DefaultKit {

	public GladiatorKit() {
		super("gladiator", "Puxe um jogador clicando com o direito nele para um luta nos ceus",
				new ItemStack(Material.IRON_FENCE), 44000,
				Arrays.asList(SwitcherKit.class, AjninKit.class, FishermanKit.class, JeenKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("gladiator"));
	}

}
