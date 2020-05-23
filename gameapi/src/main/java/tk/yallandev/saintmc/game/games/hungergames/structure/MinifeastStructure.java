package tk.yallandev.saintmc.game.games.hungergames.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.games.hungergames.structure.items.MinifeastItems;

public class MinifeastStructure implements Structure {
	
	private int radius;
	private int minSpawnDistance;
	private int border;
	private int chestLayer;
	
	public MinifeastStructure() {
		this(1, 3, 100, 500);
	}
	
	public MinifeastStructure(int chestLayer, int radius, int minSpawnDistance, int border) {
		this.chestLayer = chestLayer;
		this.radius = radius;
		this.minSpawnDistance = minSpawnDistance;
		this.border = border;
	}
	
	@Override
	public Location findPlace() {
		World w = Bukkit.getWorld("world");
		Random r = new Random();

		int x = minSpawnDistance + r.nextInt(border - minSpawnDistance);
		int z = minSpawnDistance + r.nextInt(border - minSpawnDistance);
		
		if (r.nextBoolean())
			x = -x;
		
		if (r.nextBoolean())
			z = -z;
		
		int y = w.getHighestBlockYAt(x, z) + 1;
		System.out.print("MinifeastLocation: " + x + " " + y + " " + z);
		return new Location(w, x, y, z);
	}
	
	@Override
	public void place(Location central) {
		List<Chest> chests = new ArrayList<>();
		central.clone().add(0, 1, 0).getBlock().setType(Material.ENCHANTMENT_TABLE);
		for (int x = -radius; x <= radius; x++)
			for (int z = -radius; z <= radius; z++) {
				Location feastBlock = central.clone().add(x, 0, z);
				if (central.distance(feastBlock) < radius) {
					feastBlock.getBlock().setType(Material.GLASS);
				}
				if (Math.abs(x) <= chestLayer && Math.abs(z) <= chestLayer) {
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
		List<ItemStack> items = new MinifeastItems().generateItems();
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
