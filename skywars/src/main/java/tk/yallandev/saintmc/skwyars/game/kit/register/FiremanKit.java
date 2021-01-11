package tk.yallandev.saintmc.skwyars.game.kit.register;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.skwyars.game.kit.DefaultKit;

public class FiremanKit extends DefaultKit {

	public FiremanKit() {
		super("Fireman", new ItemStack(Material.LAVA_BUCKET),
				"Utilize de suas resistências para derrotar seus inimigos!", 23000);
	}

	@Override
	public void apply(Player player) {
		player.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET, 3));
		player.getInventory().addItem(new ItemBuilder().type(Material.POTION).durability(16419).amount(3).build());
	}

}
