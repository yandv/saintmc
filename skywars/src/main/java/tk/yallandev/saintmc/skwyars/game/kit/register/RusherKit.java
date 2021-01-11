package tk.yallandev.saintmc.skwyars.game.kit.register;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.skwyars.game.kit.DefaultKit;

public class RusherKit extends DefaultKit {

	public RusherKit() {
		super("Rusher", new ItemBuilder().type(Material.POTION).amount(1).durability(16418).build(),
				"Seja o mais rápido entre eles, destrua-os em uma velocidade impressionante!", 20000);
	}

	@Override
	public void apply(Player player) {
		player.getInventory().addItem(new ItemBuilder().type(Material.POTION).amount(1).durability(16418).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.GLASS).amount(24).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.IRON_CHESTPLATE).build());
		player.getInventory()
				.addItem(new ItemBuilder().type(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL).build());
	}

}
