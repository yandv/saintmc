package br.com.saintmc.hungergames.kit.register;

import java.util.Arrays;

import org.bukkit.Material;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class BlinkKit extends DefaultKit {

	public BlinkKit() {
		super("blink", "Teletransporte-se para onde você estiver olhando",
				new ItemBuilder().type(Material.NETHER_STAR).build(), 13000,
				Arrays.asList(StomperKit.class, AjninKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("blink"));
	}

}
