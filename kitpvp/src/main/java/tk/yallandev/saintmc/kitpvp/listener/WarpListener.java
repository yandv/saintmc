
package tk.yallandev.saintmc.kitpvp.listener;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownController;
import tk.yallandev.saintmc.bukkit.api.cooldown.types.ItemCooldown;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpDeathEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpJoinEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpRespawnEvent;
import tk.yallandev.saintmc.kitpvp.gamer.Gamer;
import tk.yallandev.saintmc.kitpvp.warp.Warp;
import tk.yallandev.saintmc.kitpvp.warp.types.SpawnWarp;

public class WarpListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		GameMain.getInstance().getWarpManager().setWarp(event.getPlayer(), "spawn", true);
		event.setJoinMessage(null);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.setDeathMessage(null);
		Player entity = event.getEntity();
		Player killer = entity.getKiller();

		if (killer != null) {
			EntityDamageEvent lastDamage = entity.getLastDamageCause();

			if (lastDamage instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) lastDamage;

				if (lastDamage.getCause() == DamageCause.PROJECTILE) {
					if (entityDamageByEntityEvent.getDamager() instanceof Projectile) {
						Projectile projectile = (Projectile) entityDamageByEntityEvent.getDamager();

						if (projectile instanceof Player)
							killer = (Player) projectile.getShooter();
					}
				}
			}
		}

		Warp warp = GameMain.getInstance().getGamerManager().getGamer(entity.getUniqueId()).getWarp();

		if (warp != null) {
			Bukkit.getPluginManager().callEvent(new PlayerWarpDeathEvent(entity, killer, warp));

			if (!warp.getName().equalsIgnoreCase("1v1")) {
				for (ItemStack item : event.getDrops().stream().filter(item -> !item.getType().name().contains("_CHEST")
						&& !item.getType().name().contains("_LEGGINGS") && !item.getType().name().contains("_BOOTS")
						&& !item.getType().name().contains("_HELMET") && !item.getType().name().contains("_SWORD")
						&& !item.getType().name().contains("_AXE")).collect(Collectors.toList())) {
					entity.getWorld().dropItemNaturally(entity.getLocation(), item);
				}
			}

			event.getDrops().clear();
		}

		respawn(entity, warp);
	}

	@EventHandler
	public void onPlayerWarpDeath(PlayerWarpDeathEvent event) {
		Player killer = event.getKiller();

		if (killer != null) {
			for (ItemStack item : killer.getInventory().getArmorContents()) {
				if (item == null || item.getType() == Material.AIR)
					continue;

				item.setDurability((short) 0);
			}
		}

		CooldownController.getInstance().clearCooldown(event.getPlayer());
	}

	@EventHandler
	public void onPlayerWarpJoin(PlayerWarpJoinEvent event) {
		CooldownController.getInstance().clearCooldown(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
		Player damager = event.getDamager();

		if (damager != null) {
			ItemStack itemInHand = damager.getInventory().getItemInHand();

			if (itemInHand.getType().name().contains("SWORD") || itemInHand.getType().name().contains("AXE")) {
				itemInHand.setDurability((short) 0);
				damager.updateInventory();
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerWarpRespawn(PlayerWarpRespawnEvent event) {
		Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId());

		if (gamer == null)
			return;

		gamer.setKit(null);
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId());
		Warp warp = gamer.getWarp();

		event.setRespawnLocation(warp.getSpawnLocation());
		Bukkit.getPluginManager().callEvent(new PlayerWarpRespawnEvent(player, warp));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId());
		
		GameMain.getInstance().getWarpManager().removeWarp(gamer);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();

		if (item == null || item.getType() == Material.AIR)
			return;

		Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId());

		if (gamer.getWarp() instanceof SpawnWarp && !gamer.hasKit())
			return;

		if (item.getType() == Material.COMPASS) {
			if (CooldownController.getInstance().hasCooldown(player, "Bussola"))
				return;

			Player target = null;
			double distance = 10000;

			for (Player game : Bukkit.getOnlinePlayers().stream()
					.filter(game -> !AdminMode.getInstance().isAdmin(game)
							&& GameMain.getInstance().getGamerManager().getGamer(game.getUniqueId()).getWarp()
									.equals(gamer.getWarp())
							&& GameMain.getInstance().getGamerManager().getGamer(game.getUniqueId()).hasKit())
					.collect(Collectors.toList())) {

				double distOfPlayerToVictim = player.getLocation().distance(game.getPlayer().getLocation());
				if (distOfPlayerToVictim < distance && distOfPlayerToVictim > 25) {
					distance = distOfPlayerToVictim;
					target = game;
				}
			}

			if (target == null) {
				player.sendMessage("§cNinguém foi encontrado, bussola apontando para o spawn!");
				player.setCompassTarget(Bukkit.getWorlds().get(0).getSpawnLocation());
			} else {
				player.setCompassTarget(target.getLocation());
				player.sendMessage("§aBussola apontando para o " + target.getName() + "!");
			}

			CooldownController.getInstance().addCooldown(player, new ItemCooldown(item, "Bussola", 2l));
		}
	}

	public void respawn(Player player, Warp warp) {
		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(20);
		player.setSaturation(5);
		player.setFireTicks(0);
		player.setFallDistance(0);
		player.getActivePotionEffects().clear();
		player.setVelocity(new Vector(0, 0, 0));
		player.teleport(warp.getSpawnLocation());

		GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId()).setSpawnProtection(true);

		new BukkitRunnable() {

			@Override
			public void run() {
				Bukkit.getPluginManager().callEvent(new PlayerWarpRespawnEvent(player, warp));
			}
		}.runTaskLater(GameMain.getInstance(), 5l);
	}

}
