package tk.yallandev.saintmc.skwyars.game.kit.register;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.skwyars.game.kit.DefaultKit;

public class JasonKit extends DefaultKit {

	public JasonKit() {
		super("Jason", new ItemBuilder().type(Material.STONE_SWORD).build(),
				"Mate seus inimigos utilizando sua espada, os persiga até que não sobre mais nada!", 17000);
	}

	@Override
	public void apply(Player player) {
		player.getInventory().addItem(new ItemBuilder().type(Material.LOG).amount(16).build());
		player.getInventory()
				.addItem(new ItemBuilder().type(Material.IRON_SWORD).enchantment(Enchantment.DAMAGE_ALL, 3).build());
	}

}
