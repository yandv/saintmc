package br.com.saintmc.hungergames.kit.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class StomperKit extends DefaultKit {

	public StomperKit() {
		super("stomper", "Esmague seus inimigos", new ItemStack(Material.IRON_BOOTS), 42000,
				Arrays.asList(PhantomKit.class, GrapplerKit.class, LauncherKit.class, KangarooKit.class, BlinkKit.class,
						FlashKit.class, NinjaKit.class, ChameleonKit.class, AladdinKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("stomper"));
	}

}
