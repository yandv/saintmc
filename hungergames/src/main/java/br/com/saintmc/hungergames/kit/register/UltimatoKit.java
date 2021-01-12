package br.com.saintmc.hungergames.kit.register;

import java.util.Arrays;

import org.bukkit.Material;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class UltimatoKit extends DefaultKit {

	public UltimatoKit() {
		super("ultimato", "Enfrente seu inimigo em uma arena circular e dê um ultimato",
				new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(14).build(), 41000,
				Arrays.asList(GladiatorKit.class, JeenKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("ultimato"));
	}

}
