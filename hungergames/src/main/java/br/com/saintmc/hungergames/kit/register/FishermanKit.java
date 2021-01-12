package br.com.saintmc.hungergames.kit.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class FishermanKit extends DefaultKit {

	public FishermanKit() {
		super("fisherman", "Pesque seus oponentes com sua vara de pesca", new ItemStack(Material.FISHING_ROD), 21000,
				Arrays.asList(AjninKit.class, LauncherKit.class, PhantomKit.class, FiremanKit.class, MagmaKit.class,
						DemomanKit.class, JackhammerKit.class, GladiatorKit.class, AladdinKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("fisherman"));
	}

}
