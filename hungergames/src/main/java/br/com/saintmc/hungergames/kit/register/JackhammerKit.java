package br.com.saintmc.hungergames.kit.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class JackhammerKit extends DefaultKit {

	public JackhammerKit() {
		super("jackhammer", "Faça um buraco até e bedrock e mate seus inimigos", new ItemStack(Material.STONE_AXE),
				29000, Arrays.asList(FishermanKit.class, AjninKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("jackhammer"));
	}

}
