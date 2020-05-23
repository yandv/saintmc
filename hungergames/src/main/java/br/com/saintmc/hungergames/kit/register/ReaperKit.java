package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class ReaperKit extends DefaultKit {

	public ReaperKit() {
		super("reaper", "Use sua enxada para deixar seu inimigo com wither", new ItemStack(Material.WOOD_HOE));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("reaper"));
	}

}
