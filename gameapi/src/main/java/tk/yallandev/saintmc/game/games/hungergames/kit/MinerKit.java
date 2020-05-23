package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class MinerKit extends DefaultKit {

	public MinerKit() {
		super("miner", "Quebre minerios rapidamente", new ItemBuilder().type(Material.STONE_PICKAXE).glow().enchantment(Enchantment.DURABILITY).enchantment(Enchantment.DIG_SPEED, 2).build(), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("miner"));
	}

}
