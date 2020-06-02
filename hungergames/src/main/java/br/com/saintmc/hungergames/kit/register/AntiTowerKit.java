package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class AntiTowerKit extends DefaultKit {

	public AntiTowerKit() {
		super("antitower", "Não seja atacado pelo Stomper e nem teletransportado pelo Endermage", new ItemStack(Material.GOLD_HELMET));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("antitower"));
	}

}
