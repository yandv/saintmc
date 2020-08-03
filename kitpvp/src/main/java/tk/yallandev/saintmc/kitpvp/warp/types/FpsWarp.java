package tk.yallandev.saintmc.kitpvp.warp.types;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpJoinEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpRespawnEvent;
import tk.yallandev.saintmc.kitpvp.warp.Warp;
import tk.yallandev.saintmc.kitpvp.warp.scoreboard.types.FpsScoreboard;

public class FpsWarp extends Warp {

	public FpsWarp() {
		super("Fps", BukkitMain.getInstance().getLocationFromConfig("fps"), new FpsScoreboard());
		getWarpSettings().setSpawnProtection(true);
		getScoreboard().setWarp(this);
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

		if (GameMain.isFulliron()) {
			player.getInventory().setItem(0, new ItemBuilder().unbreakable().name("§aEspada de Diamante!")
					.type(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1).build());
			player.getInventory().setHelmet(new ItemBuilder().unbreakable().type(Material.IRON_HELMET).build());
			player.getInventory().setChestplate(new ItemBuilder().unbreakable().type(Material.IRON_CHESTPLATE).build());
			player.getInventory().setLeggings(new ItemBuilder().unbreakable().type(Material.IRON_LEGGINGS).build());
			player.getInventory().setBoots(new ItemBuilder().unbreakable().type(Material.IRON_BOOTS).build());
		} else
			player.getInventory().setItem(0, new ItemBuilder().unbreakable().name("§aEspada de Pedra!")
					.type(Material.STONE_SWORD).enchantment(Enchantment.DAMAGE_ALL).build());

		for (int x = 0; x < player.getInventory().getSize(); x++)
			player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP, 1));

		player.getInventory().setItem(13, new ItemStack(Material.RED_MUSHROOM, 64));
		player.getInventory().setItem(14, new ItemStack(Material.BROWN_MUSHROOM, 64));
		player.getInventory().setItem(15, new ItemStack(Material.BOWL, 64));
		player.updateInventory();
	}

	@Override
	public ItemStack getItem() {
		return new ItemBuilder().name("§aFps")
				.lore("\n§7Arena leve para o seu computador\n\n§a"
						+ GameMain.getInstance().getGamerManager().filter(gamer -> gamer.getWarp() == this).size()
						+ " jogadores")
				.type(Material.GLASS).build();
	}

}
