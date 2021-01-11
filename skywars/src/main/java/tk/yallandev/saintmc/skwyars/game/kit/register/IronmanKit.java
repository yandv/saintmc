package tk.yallandev.saintmc.skwyars.game.kit.register;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.skwyars.game.kit.DefaultKit;

public class IronmanKit extends DefaultKit {

	public IronmanKit() {
		super("Ironman", new ItemBuilder().type(Material.IRON_CHESTPLATE).build(),
				"Use da proteção de um golem de ferro para vencer a partida!", 25000);
	}

	@Override
	public void apply(Player player) {
		player.getInventory().addItem(new ItemBuilder().type(Material.IRON_HELMET)
				.enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.IRON_CHESTPLATE)
				.enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.IRON_LEGGINGS)
				.enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.IRON_BOOTS)
				.enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
	}

}
