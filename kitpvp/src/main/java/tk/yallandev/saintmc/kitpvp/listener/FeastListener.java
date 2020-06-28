package tk.yallandev.saintmc.kitpvp.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import lombok.AllArgsConstructor;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.common.utils.string.StringUtils;
import tk.yallandev.saintmc.kitpvp.GameMain;

public class FeastListener implements Listener {

	private int feastTimer;
	private List<Item> itemList;

	public FeastListener() {
		feastTimer = 240;
		itemList = new ArrayList<>();

		itemList.add(
				new Item(new ItemBuilder().type(Material.GOLDEN_APPLE).name("§aGolden Apple").amount(3).build(), 80));
		itemList.add(new Item(new ItemBuilder().type(Material.EXP_BOTTLE).name("§aGolden Apple").build(), 80));

		if (GameMain.isFulliron()) {
			itemList.add(new Item(new ItemBuilder().type(Material.DIAMOND_SWORD).name("§aEspada de Diamante")
					.enchantment(Enchantment.DAMAGE_ALL, 1).enchantment(Enchantment.FIRE_ASPECT).build(), 80));

			itemList.add(new Item(new ItemBuilder().type(Material.DIAMOND_HELMET).build(), 20));
			itemList.add(new Item(new ItemBuilder().type(Material.DIAMOND_CHESTPLATE).build(), 10));
			itemList.add(new Item(new ItemBuilder().type(Material.DIAMOND_LEGGINGS).build(), 10));
			itemList.add(new Item(new ItemBuilder().type(Material.DIAMOND_BOOTS).build(), 20));

			itemList.add(new Item(new ItemBuilder().type(Material.IRON_HELMET)
					.enchantment(Enchantment.PROTECTION_ENVIRONMENTAL).build(), 60));
			itemList.add(new Item(new ItemBuilder().type(Material.IRON_CHESTPLATE)
					.enchantment(Enchantment.PROTECTION_ENVIRONMENTAL).build(), 50));
			itemList.add(new Item(new ItemBuilder().type(Material.IRON_LEGGINGS)
					.enchantment(Enchantment.PROTECTION_ENVIRONMENTAL).build(), 50));
			itemList.add(new Item(new ItemBuilder().type(Material.IRON_BOOTS)
					.enchantment(Enchantment.PROTECTION_ENVIRONMENTAL).build(), 60));
		} else {
			itemList.add(new Item(new ItemBuilder().type(Material.LEATHER_HELMET).build(), 70));
			itemList.add(new Item(new ItemBuilder().type(Material.LEATHER_CHESTPLATE).build(), 50));
			itemList.add(new Item(new ItemBuilder().type(Material.LEATHER_LEGGINGS).build(), 50));
			itemList.add(new Item(new ItemBuilder().type(Material.LEATHER_BOOTS).build(), 70));

			itemList.add(new Item(new ItemBuilder().type(Material.CHAINMAIL_HELMET).build(), 50));
			itemList.add(new Item(new ItemBuilder().type(Material.CHAINMAIL_CHESTPLATE).build(), 30));
			itemList.add(new Item(new ItemBuilder().type(Material.CHAINMAIL_LEGGINGS).build(), 30));
			itemList.add(new Item(new ItemBuilder().type(Material.CHAINMAIL_BOOTS).build(), 50));
		}
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND) {
			feastTimer++;

			if (feastTimer > 300) {
				if (feastTimer == 720) {
					feastTimer = 0;
				} else if (feastTimer == 420) {
					destroyFeast();
				}
			} else {
				if (feastTimer == 300) {
					spawnFeast();
					return;
				}

				if (feastTimer >= 295) {
					broadcastTimer();
					return;
				}

				if (feastTimer >= 120 && feastTimer % 60 == 0) {
					broadcastTimer();
				}
			}
		}

	}

	void broadcastTimer() {
		Bukkit.broadcastMessage(
				"§1§lFEAST §fO feast irá spawnar em §a" + StringUtils.formatTime(300 - feastTimer) + "§f!");
	}

	void spawnFeast() {
		for (int x = 0; x <= 12; x++) {
			Location location = BukkitMain.getInstance().getLocationFromConfig("feast-chest-" + x, null);

			if (location == null)
				continue;

			location.getBlock().setType(Material.CHEST);

			if (location.getBlock().getState() instanceof Chest) {
				fillChest((Chest) location.getBlock().getState());
			}
		}

		Location lighthingStrike = BukkitMain.getInstance().getLocationFromConfig("enchantment-table");
		lighthingStrike.getWorld().strikeLightning(lighthingStrike);
		lighthingStrike.getBlock().setType(Material.ENCHANTMENT_TABLE);

		Bukkit.broadcastMessage("§1§lFEAST §fO feast spawnou!");
	}

	void fillChest(Chest chest) {
		for (int x = 0; x < CommonConst.RANDOM.nextInt(3) + 2; x++) {
			Item item = itemList.get(CommonConst.RANDOM.nextInt(itemList.size()));

			if (CommonConst.RANDOM.nextInt(100) <= item.chance) {
				int slot;

				do {
					slot = CommonConst.RANDOM.nextInt(chest.getInventory().getSize());
				} while (chest.getInventory().getItem(slot) != null);

				chest.getInventory().setItem(slot, item.itemStack);
			}
		}
	}

	void destroyFeast() {
		for (int x = 0; x <= 12; x++) {
			Location location = BukkitMain.getInstance().getLocationFromConfig("feast-chest-" + x, null);

			if (location == null)
				continue;

			location.getBlock().setType(Material.AIR);
		}

		BukkitMain.getInstance().getLocationFromConfig("enchantment-table").getBlock().setType(Material.AIR);
	}

	@AllArgsConstructor
	class Item {

		private ItemStack itemStack;
		private int chance;

	}

}
