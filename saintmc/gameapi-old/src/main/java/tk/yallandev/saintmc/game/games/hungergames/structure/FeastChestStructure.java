package tk.yallandev.saintmc.game.games.hungergames.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.games.hungergames.structure.items.FeastItems;

public class FeastChestStructure implements Structure {
	@Override
	public Location findPlace() {
		return null;
	}

	@Override
	public void place(Location central) {
		List<Chest> chests = new ArrayList<>();
		central.clone().add(0, 1, 0).getBlock().setType(Material.ENCHANTMENT_TABLE);
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				if (!(x == 0 && z == 0) && ((x % 2 == 0 && z % 2 == 0) || (x % 2 != 0 && z % 2 != 0))) {
					Location loc = central.clone().add(x, 1, z);
					loc.getBlock().setType(Material.CHEST);
					if (loc.getBlock().getType() == Material.CHEST) {
						Block b = loc.getBlock();
						if (b.getState() instanceof Chest)
							chests.add((Chest) loc.getBlock().getState());
					}
				}
			}
		}
		if (chests.size() <= 0)
			return;
		List<ItemStack> items = new FeastItems().generateItems();
		Iterator<ItemStack> iterator = items.iterator();
		int chestNumber = 0;
		while (iterator.hasNext() && chests.size() > 0) {
			if (chestNumber >= chests.size())
				chestNumber = 0;
			Chest chest = chests.get(chestNumber);
			if (chest == null) {
				chestNumber = 0;
				continue;
			}
			Inventory inv = chest.getBlockInventory();
			if (inv.firstEmpty() == -1) {
				chests.remove(chestNumber);
				continue;
			}
			inv.addItem(iterator.next());
			chest.update();
			iterator.remove();
			++chestNumber;
		}
	}

}
