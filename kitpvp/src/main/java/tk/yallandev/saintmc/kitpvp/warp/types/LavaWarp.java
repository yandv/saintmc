package tk.yallandev.saintmc.kitpvp.warp.types;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpJoinEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpRespawnEvent;
import tk.yallandev.saintmc.kitpvp.warp.Warp;

public class LavaWarp extends Warp {

	public LavaWarp() {
		super("Lava", BukkitMain.getInstance().getLocationFromConfig("lava"));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			Player player = (Player) event.getEntity();

			if (inWarp(player)) {
				if (event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK
						|| event.getCause() == DamageCause.LAVA)
					event.setCancelled(false);
				else
					event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerWarpJoin(PlayerWarpJoinEvent event) {
		Player player = event.getPlayer();

		if (event.getWarp() != this)
			return;

		handleInventory(player);
	}

	@EventHandler
	public void onPlayerWarpRespawn(PlayerWarpRespawnEvent event) {
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

		player.getInventory().setItem(0,
				new ItemBuilder().unbreakable().name("§aEspada de Pedra!").type(Material.STONE_SWORD).build());

		for (int x = 0; x < player.getInventory().getSize(); x++)
			player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP, 1));

		player.getInventory().setItem(13, new ItemStack(Material.RED_MUSHROOM, 64));
		player.getInventory().setItem(14, new ItemStack(Material.BROWN_MUSHROOM, 64));
		player.getInventory().setItem(15, new ItemStack(Material.BOWL, 64));
		player.updateInventory();
	}

	@Override
	public ItemStack getItem() {
		return new ItemBuilder().name("§aLava Challenge")
				.lore("\n§7Treine seu refil e recraft complentando\nos desafios de lava propostos.\n\n§a" + GameMain.getInstance().getGamerManager().filter(gamer -> gamer.getWarp() == this).size() + " jogadores")
				.type(Material.LAVA_BUCKET).build();
	}

}
