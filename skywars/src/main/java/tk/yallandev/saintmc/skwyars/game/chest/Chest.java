package tk.yallandev.saintmc.skwyars.game.chest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Chest {

	private static final Random RANDOM = new Random();

	private int x;
	private int y;
	private int z;

	private ChestType chestType;

	/**
	 * Clear chest
	 * 
	 * @param world
	 */

	public void clear(World world) {
		getChest(world).getBlockInventory().clear();
	}

	/**
	 * Fill the chest
	 * 
	 * @param world
	 */

	public void fill(World world) {
		org.bukkit.block.Chest chest = getChest(world);

		Iterator<ItemStack> iterator = generateItems().iterator();

		while (iterator.hasNext()) {
			Inventory inv = chest.getBlockInventory();

			if (inv.firstEmpty() == -1)
				continue;

			ItemStack item = iterator.next();

			if (item.getAmount() >= 0) {

				int slot = RANDOM.nextInt(inv.getSize());

				while (!(chest.getBlockInventory().getItem(slot) == null
						|| chest.getBlockInventory().getItem(slot).getType() == Material.AIR)) {
					slot = RANDOM.nextInt(inv.getSize());
				}

				chest.getBlockInventory().setItem(slot, item);

				chest.update();
			}

			iterator.remove();
		}
	}

	public org.bukkit.block.Chest getChest(World world) {
		Block block = new Location(world, x, y, z).getBlock();

		if (block.getType() != Material.CHEST)
			block.setType(Material.CHEST);

		return (org.bukkit.block.Chest) block.getState();
	}

	public List<ItemStack> generateItems() {
		List<ItemStack> feastItems = new ArrayList<>();

		switch (chestType) {
		case DEFAULT: {

			int times = 0;

			if (addItem(new ItemStack(Material.DIAMOND_HELMET), 30, feastItems))
				times++;
			else
				addItem(new ItemStack(Material.IRON_HELMET), 100, feastItems);

			if (addItem(new ItemStack(Material.DIAMOND_CHESTPLATE), 30, feastItems))
				times++;
			else
				addItem(new ItemStack(Material.IRON_CHESTPLATE), 100, feastItems);

			if (times < 2 && addItem(new ItemStack(Material.DIAMOND_LEGGINGS), 30, feastItems))
				times++;
			else
				addItem(new ItemStack(Material.IRON_LEGGINGS), 100, feastItems);

			if (times < 2 && addItem(new ItemStack(Material.DIAMOND_BOOTS), 30, feastItems))
				times++;
			else
				addItem(new ItemStack(Material.IRON_BOOTS), 100, feastItems);

			break;
		}
		case DEFAULT_2: {
			addItem(new ItemStack(Material.IRON_AXE), 90, feastItems);
			addItem(new ItemStack(Material.IRON_PICKAXE), 90, feastItems);

			addItem(new ItemStack(Material.COOKED_BEEF, RANDOM.nextBoolean() ? 8 : 16), 80, feastItems);
			addItem(new ItemStack(Material.COOKED_BEEF, RANDOM.nextBoolean() ? 8 : 16), 80, feastItems);

			addItem(new ItemStack(Material.WOOD, RANDOM.nextBoolean() ? 16 : 32), 100, feastItems);
			break;
		}
		case DEFAULT_3: {
//			if (!addItem(new ItemBuilder().type(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1)
//					.name("§cEspada de Diamante").build(), 30, feastItems))
//				if (!addItem(new ItemBuilder().type(Material.DIAMOND_SWORD).name("§aEspada de Diamante").build(), 70,
//						feastItems))
//					addItem(new ItemBuilder().type(Material.IRON_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1)
//							.name("§aEspada de Ferro").build(), 100, feastItems);

			addItem(new ItemStack(Material.EGG, 8), 80, feastItems);
			addItem(new ItemStack(Material.FISHING_ROD), 55, feastItems);

			addItem(new ItemStack(Material.STONE, RANDOM.nextBoolean() ? 16 : 32), 80, feastItems);
			break;
		}
		case MINIFEAST: {

			int times = 0;

//			if (addItem(new ItemBuilder().type(Material.DIAMOND_CHESTPLATE)
//					.enchantment(Enchantment.PROTECTION_ENVIRONMENTAL).build(), 49, feastItems))
//				times++;

//			if (addItem(new ItemBuilder().type(Material.DIAMOND_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL)
//					.build(), 32, feastItems))
//				times++;

			if (addItem(new ItemStack(Material.DIAMOND_LEGGINGS), 30, feastItems))
				times++;

			if (addItem(new ItemStack(Material.DIAMOND_HELMET), 51, feastItems))
				times++;

//			if (addItem(new ItemBuilder().type(Material.BOW).enchantment(Enchantment.ARROW_DAMAGE, 1)
//					.name("§cArco Superior").build(), 68, feastItems))
//				times++;

			if (times >= 4)
				break;

			if (addItem(new ItemStack(Material.GOLDEN_APPLE, RANDOM.nextBoolean() ? 3 : 6), 64, feastItems))
				times++;

			if (addItem(new ItemStack(Material.ENDER_PEARL, RANDOM.nextBoolean() ? 1 : 2), 28, feastItems))
				times++;
			if (addItem(new ItemStack(Material.ARROW, RANDOM.nextBoolean() ? 16 : 32), 100, feastItems))
				times++;

			if (addItem(new ItemStack(Material.EXP_BOTTLE, RANDOM.nextBoolean() ? 16 : 32), 82, feastItems))
				times++;

			if (times >= 4)
				break;

			if (addItem(new ItemStack(Material.COMPASS), 91, feastItems))
				times++;

			if (addItem(new ItemStack(Material.POTION, 1, (short) 16417), 91, feastItems))
				times++;

			if (addItem(new ItemStack(Material.POTION, 1, (short) 16418), 91, feastItems))
				times++;

			if (times >= 4)
				break;

//			if (addItem(new ItemBuilder().type(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1)
//					.name("§cEspada de Diamante").build(), 23, feastItems))
//				times++;
			break;
		}
		case FEAST: {
			addItem(new ItemStack(Material.WATER_BUCKET), 55, feastItems);
			addItem(new ItemStack(Material.LAVA_BUCKET), 55, feastItems);

//			if (!addItem(new ItemBuilder().type(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 2)
//					.enchantment(Enchantment.FIRE_ASPECT).name("§4Espada de Diamante").build(), 40, feastItems))
//				addItem(new ItemBuilder().type(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1)
//						.name("§cEspada de Diamante").build(), 80, feastItems);

//			if (!addItem(new ItemBuilder().type(Material.BOW).enchantment(Enchantment.ARROW_DAMAGE, 3)
//					.name("§cArco Superior").build(), 30, feastItems))
//				addItem(new ItemBuilder().type(Material.BOW).enchantment(Enchantment.ARROW_DAMAGE, 1)
//						.name("§4Arco Superior").build(), 40, feastItems);
			addItem(new ItemStack(Material.DIAMOND_HELMET), 42, feastItems);
			addItem(new ItemStack(Material.DIAMOND_CHESTPLATE), 48, feastItems);
			addItem(new ItemStack(Material.DIAMOND_LEGGINGS), 38, feastItems);
			addItem(new ItemStack(Material.DIAMOND_BOOTS), 52, feastItems);
			addItem(new ItemStack(Material.COBBLESTONE, RANDOM.nextBoolean() ? 32 : 64), 80, feastItems);
			addItem(new ItemStack(Material.GOLDEN_APPLE, RANDOM.nextBoolean() ? 3 : 5), 38, feastItems);
			addItem(new ItemStack(Material.COMPASS), 35, feastItems);
			break;
		}
		}

		Collections.shuffle(feastItems);
		return feastItems;
	}

	private boolean addItem(ItemStack mat, int chance, List<ItemStack> items) {
		if (RANDOM.nextInt(100) + 1 <= chance) {
			items.add(mat);
			return true;
		}

		return false;
	}

	/**
	 * Prevent duplicated chest
	 */

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Chest) {
			Chest chest = (Chest) obj;

			return chest.getX() == getX() && chest.getY() == getY() && chest.getZ() == getZ();
		}

		return super.equals(obj);
	}

}