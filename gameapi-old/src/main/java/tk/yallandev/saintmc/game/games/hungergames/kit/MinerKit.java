package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class MinerKit extends DefaultKit {

	public MinerKit() {
		super("miner", "§%ability-miner-description%§", new ItemBuilder().type(Material.STONE_PICKAXE).glow().enchantment(Enchantment.DURABILITY).enchantment(Enchantment.DIG_SPEED, 2).build(), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("miner"));
	}

}
