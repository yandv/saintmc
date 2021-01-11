package tk.yallandev.saintmc.skwyars.game.kit.register;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.skwyars.game.kit.DefaultKit;

public class PaladinoKit extends DefaultKit {

	public PaladinoKit() {
		super("Paladino", new ItemStack(Material.DIAMOND_SWORD),
				"Seja um verdadeiro paladino e destrua todos seus oponentes!", 20000);
	}

	@Override
	public void apply(Player player) {
		player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
		player.getInventory().addItem(new ItemStack(Material.IRON_LEGGINGS));
		player.getInventory().addItem(new ItemBuilder().name("§aKit Paladino").type(Material.DIAMOND_SWORD)
				.enchantment(Enchantment.DAMAGE_ALL).build());
	}

}