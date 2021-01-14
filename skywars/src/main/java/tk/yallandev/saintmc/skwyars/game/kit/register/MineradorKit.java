package tk.yallandev.saintmc.skwyars.game.kit.register;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.skwyars.game.kit.DefaultKit;

public class MineradorKit extends DefaultKit {

	public MineradorKit() {
		super("Minerador", new ItemStack(Material.DIAMOND_PICKAXE),
				"Use sua experiencia em mineração para combater seus inimigos!", 17000, 450);
	}

	@Override
	public void apply(Player player) {
		player.getInventory().addItem(new ItemBuilder().type(Material.DIAMOND_PICKAXE).amount(1)
				.enchantment(Enchantment.DIG_SPEED, 2).build());
	}

}
