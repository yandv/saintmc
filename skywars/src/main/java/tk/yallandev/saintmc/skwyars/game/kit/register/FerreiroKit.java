package tk.yallandev.saintmc.skwyars.game.kit.register;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.skwyars.game.kit.DefaultKit;

public class FerreiroKit extends DefaultKit {

	public FerreiroKit() {
		super("Ferreiro", new ItemStack(Material.ANVIL),
				"Use sua experiencia em forjas para melhorar ou fundir seus equipamentos!", 15000, 450);
	}

	@Override
	public void apply(Player player) {
		player.getInventory().addItem(new ItemBuilder().type(Material.EXP_BOTTLE).amount(64).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.ANVIL).amount(1).build());
		player.getInventory().addItem(
				new ItemBuilder().type(Material.ENCHANTED_BOOK).enchantment(Enchantment.DAMAGE_ALL).amount(3).build());
	}

}
