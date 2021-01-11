package tk.yallandev.saintmc.skwyars.game.kit.register;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.skwyars.game.kit.DefaultKit;

public class BarbaroKit extends DefaultKit {

	public BarbaroKit() {
		super("Barbaro", new ItemBuilder().type(Material.STONE_AXE).build(),
				"Lute como um verdadeiro Bárbaro, acabe com seus oponentes utilizando força bruta!", 17000);
	}

	@Override
	public void apply(Player player) {
		player.getInventory().addItem(new ItemBuilder().name("§aForça do Barbaro").type(Material.POTION).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.DIAMOND_SWORD).build());
	}

}
