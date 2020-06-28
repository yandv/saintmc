package tk.yallandev.saintmc.kitpvp.manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Preconditions;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.common.utils.ClassGetter;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.kit.PlayerSelectKitEvent;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class KitManager {

	private List<Kit> kitList;
	private Kit defaultKit;

	public KitManager() {
		kitList = new ArrayList<>();

		for (Class<?> kitClass : ClassGetter.getClassesForPackage(GameMain.getInstance().getClass(),
				"tk.yallandev.saintmc.kitpvp.kit.register")) {
			if (Kit.class.isAssignableFrom(kitClass)) {
				try {
					Kit kit = (Kit) kitClass.newInstance();
					addKit(kit);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.print("Erro ao carregar o kit " + kitClass.getSimpleName());
				}
			}
		}

		kitList.sort((o1, o2) -> o1.getKitName().compareTo(o2.getKitName()));

		Kit item = kitList.stream().filter(kit -> kit.getKitName().equalsIgnoreCase("pvp")).findFirst().orElse(null);
		int itemPos = kitList.indexOf(item);
		defaultKit = item;
		kitList.remove(itemPos);
		kitList.add(0, item);
	}

	public void addKit(Kit kit) {
		kitList.add(kit);
		kit.register();
	}

	public Kit getKit(String kitName) {
		return kitList.stream().filter(kit -> kit.getKitName().equalsIgnoreCase(kitName)).findFirst().orElse(null);
	}

	public void selectKit(Player player, Kit kit) {
		Preconditions.checkArgument(kit != null, "Kit cannot be null");

		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);

		if (GameMain.isFulliron()) {
			player.getInventory().setItem(0,
					kit.getKitName().equalsIgnoreCase("pvp")
							? new ItemBuilder().unbreakable().type(Material.DIAMOND_SWORD)
									.enchantment(Enchantment.DAMAGE_ALL, 1).build()
							: new ItemBuilder().unbreakable().type(Material.IRON_SWORD)
									.enchantment(Enchantment.DAMAGE_ALL, 1).build());
			player.getInventory().setHelmet(new ItemBuilder().unbreakable().type(Material.IRON_HELMET).build());
			player.getInventory().setChestplate(new ItemBuilder().unbreakable().type(Material.IRON_CHESTPLATE).build());
			player.getInventory().setLeggings(new ItemBuilder().unbreakable().type(Material.IRON_LEGGINGS).build());
			player.getInventory().setBoots(new ItemBuilder().unbreakable().type(Material.IRON_BOOTS).build());
		} else
			player.getInventory().setItem(0,
					kit.getKitName().equalsIgnoreCase("pvp")
							? new ItemBuilder().unbreakable().type(Material.STONE_SWORD)
									.enchantment(Enchantment.DAMAGE_ALL).build()
							: new ItemBuilder().unbreakable().type(Material.STONE_SWORD).build());

		player.getInventory().setItem(13, new ItemStack(Material.BROWN_MUSHROOM, 64));
		player.getInventory().setItem(14, new ItemStack(Material.RED_MUSHROOM, 64));
		player.getInventory().setItem(15, new ItemStack(Material.BOWL, 64));

		for (int x = 0; x < player.getInventory().getSize(); x++)
			player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP, 1));

		player.getInventory().setItem(8, new ItemStack(Material.COMPASS));

		kit.applyKit(player);
		Bukkit.getPluginManager().callEvent(new PlayerSelectKitEvent(player, kit));
	}

	public List<Kit> getKitList() {
		return kitList;
	}

	public Kit getDefaultKit() {
		return defaultKit;
	}

}
