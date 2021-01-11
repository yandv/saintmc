package tk.yallandev.saintmc.skwyars.game.kit.register;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.skwyars.game.kit.DefaultKit;

public class PescadorKit extends DefaultKit {

	public PescadorKit() {
		super("Pescador", new ItemBuilder().type(Material.FISHING_ROD).build(),
				"Com sua vara empurre seus oponentes para longe!", 19000);
	}

	@Override
	public void apply(Player player) {
		player.getInventory().addItem(new ItemBuilder().type(Material.DIAMOND_CHESTPLATE).amount(1).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.GOLDEN_APPLE).amount(3).build());
		player.getInventory()
				.addItem(new ItemBuilder().type(Material.FISHING_ROD).enchantment(Enchantment.KNOCKBACK, 2).build());
	}

}
