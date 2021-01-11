package tk.yallandev.saintmc.skwyars.game.kit.register;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.skwyars.game.kit.DefaultKit;

public class GuardiaoKit extends DefaultKit {

	public GuardiaoKit() {
		super("Guardião", new ItemBuilder().type(Material.DIAMOND_BOOTS).build(),
				"Avance contra seus inimigos e os destrua com suas habilidades!", 26000);
	}

	@Override
	public void apply(Player player) {
		player.getInventory().addItem(new ItemBuilder().type(Material.DIAMOND_CHESTPLATE).amount(1).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.DIAMOND_BOOTS).amount(1).build());
		player.getInventory()
				.addItem(new ItemBuilder().type(Material.WOOD_SWORD).enchantment(Enchantment.DAMAGE_ALL, 4).build());
	}

}
