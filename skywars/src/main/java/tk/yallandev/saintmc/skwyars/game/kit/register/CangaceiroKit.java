package tk.yallandev.saintmc.skwyars.game.kit.register;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.skwyars.game.kit.DefaultKit;

public class CangaceiroKit extends DefaultKit {

	public CangaceiroKit() {
		super("Cangaceiro", new ItemStack(Material.LEATHER_CHESTPLATE),
				"Com o seu facão, arranque a cabeça de seus oponentes fora!", 30000);
	}

	@Override
	public void apply(Player player) {
		player.getInventory().addItem(new ItemBuilder().type(Material.LEATHER_CHESTPLATE).amount(1).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.LEATHER_LEGGINGS).amount(1).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.DIAMOND_BOOTS).amount(1).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.DIAMOND_HELMET).amount(1).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.DIAMOND_SWORD)
				.enchantment(Enchantment.DAMAGE_ALL, 3).amount(3).build());
	}

}