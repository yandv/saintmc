package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class MinerKit extends DefaultKit {

	public MinerKit() {
		super("miner", "Quebre minerios rapidamente", new ItemBuilder().type(Material.STONE_PICKAXE).glow()
				.enchantment(Enchantment.DURABILITY).enchantment(Enchantment.DIG_SPEED, 2).build(), 29000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("miner"));
	}

}
