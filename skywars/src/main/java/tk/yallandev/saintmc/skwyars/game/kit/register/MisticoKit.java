package tk.yallandev.saintmc.skwyars.game.kit.register;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.skwyars.game.kit.DefaultKit;

public class MisticoKit extends DefaultKit {

	public MisticoKit() {
		super("Mistico", new ItemStack(Material.ENCHANTMENT_TABLE),
				"Use suas habilidades místicas para fortalecer seus equipamentos!", 15000);
	}

	@Override
	public void apply(Player player) {
		player.getInventory().addItem(new ItemBuilder().type(Material.EXP_BOTTLE).amount(64).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.ENCHANTMENT_TABLE).amount(1).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.BOOKSHELF).amount(16).build());
	}

}
