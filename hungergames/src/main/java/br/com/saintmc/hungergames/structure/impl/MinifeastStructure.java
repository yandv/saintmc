package br.com.saintmc.hungergames.structure.impl;

import java.util.ArrayList;
import java.util.Collections;
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

import br.com.saintmc.hungergames.structure.Chestable;
import br.com.saintmc.hungergames.structure.Structure;
import tk.yallandev.saintmc.CommonConst;

public class MinifeastStructure implements Structure, Chestable {
	
	private int radius;
	private int minSpawnDistance;
	private int border;
	private int chestLayer;
	
	private List<Chest> chests;
	
	public MinifeastStructure() {
		this(1, 3, 100, 500);
	}
	
	public MinifeastStructure(int chestLayer, int radius, int minSpawnDistance, int border) {
		this.chestLayer = chestLayer;
		this.radius = radius;
		this.minSpawnDistance = minSpawnDistance;
		this.border = border;
		this.chests = new ArrayList<>();
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
		return new Location(w, x, y, z);
	}

	@Override
	public void spawn(Location location) {
		location.clone().add(0, 1, 0).getBlock().setType(Material.ENCHANTMENT_TABLE);
		
		for (int x = -radius; x <= radius; x++)
			for (int z = -radius; z <= radius; z++) {
				Location feastBlock = location.clone().add(x, 0, z);
				
				if (location.distance(feastBlock) < radius) {
					feastBlock.getBlock().setType(Material.GLASS);
				}
				
				if (Math.abs(x) <= chestLayer && Math.abs(z) <= chestLayer) {
					if (!(x == 0 && z == 0) && ((x % 2 == 0 && z % 2 == 0) || (x % 2 != 0 && z % 2 != 0))) {
						Location loc = location.clone().add(x, 1, z);
						loc.getBlock().setType(Material.CHEST);
						
						if (loc.getBlock().getType() == Material.CHEST) {
							Block b = loc.getBlock();
							
							if (b.getState() instanceof Chest)
								chests.add((Chest) loc.getBlock().getState());
						}
					}
				}
			}
		
		spawnChest(location);
		System.out.print("MinifeastLocation: " + location.getX() + " " + location.getY() + " " + location.getZ());
	}
	
	@Override
	public void spawnChest(Location locaion) {
		if (chests.size() <= 0)
			return;
		
		List<ItemStack> items = generateItems();
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
	
	public List<ItemStack> generateItems() {
		List<ItemStack> feastItems = new ArrayList<>();
		feastItems.addAll(addItem(Material.IRON_SWORD, 1, 2));
		feastItems.addAll(addItem(Material.DIAMOND, 2, 5));
		feastItems.addAll(addItem(Material.IRON_INGOT, 4, 8));
		feastItems.addAll(addItem(Material.BREAD, 10, 20));
		feastItems.addAll(addItem(Material.INK_SACK, (short) 3, 6, 10));
		feastItems.addAll(addItem(Material.MUSHROOM_SOUP, 6, 10));
		feastItems.addAll(addItem(Material.WEB, 4, 8));
		feastItems.addAll(addItem(Material.TNT, 4, 10));
		feastItems.addAll(addItem(Material.FLINT_AND_STEEL, 2, 4));
		feastItems.addAll(addItem(Material.EXP_BOTTLE, 12, 24));
		feastItems.addAll(addItem(Material.INK_SACK, (short) 4, 12, 24));
		feastItems.addAll(addItem(Material.POTION, (short) 16386, 0, 1));
		feastItems.addAll(addItem(Material.POTION, (short) 16388, 0, 1));
		feastItems.addAll(addItem(Material.POTION, (short) 16394, 0, 1));
		feastItems.addAll(addItem(Material.POTION, (short) 16396, 0, 1));
		Collections.shuffle(feastItems);
		return feastItems;
	}

	private List<ItemStack> addItem(Material mat, int min, int max) {
		return addItem(mat, (short) 0, min, max);
	}

	private List<ItemStack> addItem(Material mat, short durability, int min, int max) {
		List<ItemStack> items = new ArrayList<>();
		for (int i = 0; i <= min + CommonConst.RANDOM.nextInt(max - min); i++) {
			items.add(new ItemStack(mat, 1, durability));
		}
		return items;
	}

}
