package br.com.saintmc.hungergames.kit.register;

import java.util.Arrays;

import org.bukkit.Material;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class MagmaKit extends DefaultKit {

	public MagmaKit() {
		super("magma", "Tenha 33% de chance de colocar fogo em quem você bater",
				new ItemBuilder().type(Material.LAVA_BUCKET).build(), 29000,
				Arrays.asList(AjninKit.class, FishermanKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("magma"));
	}

}
