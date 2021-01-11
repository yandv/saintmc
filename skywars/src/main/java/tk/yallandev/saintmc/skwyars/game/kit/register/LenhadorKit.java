package tk.yallandev.saintmc.skwyars.game.kit.register;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.skwyars.game.kit.DefaultKit;

public class LenhadorKit extends DefaultKit {

	public LenhadorKit() {
		super("Lenhador", new ItemBuilder().type(Material.DIAMOND_AXE).build(),
				"Use seu machado e suas madeiras para cortar seus oponentes até o fim!", 20000);
	}

	@Override
	public void apply(Player player) {
		player.getInventory().addItem(new ItemBuilder().type(Material.LOG).amount(64).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.IRON_CHESTPLATE).build());
		player.getInventory()
				.addItem(new ItemBuilder().type(Material.DIAMOND_AXE).enchantment(Enchantment.DAMAGE_ALL, 3).build());
	}

}
