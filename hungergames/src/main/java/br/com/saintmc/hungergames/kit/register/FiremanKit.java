package br.com.saintmc.hungergames.kit.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class FiremanKit extends DefaultKit {

	public FiremanKit() {
		super("fireman", "Não receba dano de fogo ou lava", new ItemStack(Material.LAVA_BUCKET), 24000,
				Arrays.asList(AjninKit.class, FishermanKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("fireman"));
	}

}
