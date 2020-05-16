package tk.yallandev.saintmc.kitpvp.warp.types;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpJoinEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpRespawnEvent;
import tk.yallandev.saintmc.kitpvp.warp.Warp;

public class MainWarp extends Warp {

	public MainWarp() {
		super("Main", BukkitMain.getInstance().getLocationFromConfig("main"));
		getWarpSettings().setSpawnProtection(true);
	}

	@EventHandler
	public void onPlayerWarpRespawn(PlayerWarpRespawnEvent event) {
		Player player = event.getPlayer();

		if (event.getWarp() != this)
			return;

		handleInventory(player);
	}

	@EventHandler
	public void onPlayerWarpJoin(PlayerWarpJoinEvent event) {
		Player player = event.getPlayer();

		if (event.getWarp() != this)
			return;

		handleInventory(player);
	}

	private void handleInventory(Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);

		for (PotionEffect potion : player.getActivePotionEffects())
			player.removePotionEffect(potion.getType());

		player.setLevel(0);
		player.setFoodLevel(20);
		player.setHealth(20D);
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999*20, 0));
		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999*20, 1));

		player.getInventory().setItem(0, new ItemBuilder().unbreakable().name("§aEspada de Diamante!")
				.type(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1).build());
		player.getInventory().setHelmet(new ItemBuilder().unbreakable().type(Material.IRON_HELMET).build());
		player.getInventory().setChestplate(new ItemBuilder().unbreakable().type(Material.IRON_CHESTPLATE).build());
		player.getInventory().setLeggings(new ItemBuilder().unbreakable().type(Material.IRON_LEGGINGS).build());
		player.getInventory().setBoots(new ItemBuilder().unbreakable().type(Material.IRON_BOOTS).build());

		for (int x = 0; x < player.getInventory().getSize(); x++)
			player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP, 1));

		player.getInventory().setItem(13, new ItemStack(Material.RED_MUSHROOM, 64));
		player.getInventory().setItem(14, new ItemStack(Material.BROWN_MUSHROOM, 64));
		player.getInventory().setItem(15, new ItemStack(Material.BOWL, 64));
	}

	@Override
	public ItemStack getItem() {
		return new ItemBuilder().name("§aMain")
				.lore("eae")
				.type(Material.GLASS).build();
	}
}
