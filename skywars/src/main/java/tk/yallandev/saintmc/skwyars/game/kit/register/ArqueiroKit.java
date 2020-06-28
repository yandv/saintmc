package tk.yallandev.saintmc.skwyars.game.kit.register;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.skwyars.game.kit.DefaultKit;

public class ArqueiroKit extends DefaultKit {

	public ArqueiroKit() {
		super("Arqueiro", new ItemStack(Material.ARROW),
				"Honre as habilidades de seus antepassados, e destrua-os com seu poderoso arco!", 6500, 450);
	}

	@Override
	public void apply(Player player) {
		player.getInventory().addItem(new ItemStack(Material.IRON_CHESTPLATE));
		player.getInventory().addItem(new ItemBuilder().name("§aKit Arqueiro").type(Material.BOW)
				.enchantment(Enchantment.ARROW_DAMAGE, 2).enchantment(Enchantment.ARROW_KNOCKBACK).build());
		player.getInventory().addItem(new ItemStack(Material.ARROW, 16));
	}

}