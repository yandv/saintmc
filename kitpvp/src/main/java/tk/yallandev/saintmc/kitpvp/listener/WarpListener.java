
package tk.yallandev.saintmc.kitpvp.listener;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpDeathEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpRespawnEvent;
import tk.yallandev.saintmc.kitpvp.gamer.Gamer;
import tk.yallandev.saintmc.kitpvp.warp.Warp;

public class WarpListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId());

		GameMain.getInstance().getWarpManager().setWarp(gamer, "spawn", true);

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

		Bukkit.getPluginManager().callEvent(new PlayerWarpDeathEvent(entity, killer, warp));

		if (!warp.getName().equalsIgnoreCase("1v1")) {
			for (ItemStack item : event.getDrops().stream()
					.filter(item -> !item.getType().name().contains("_CHEST")
							&& !item.getType().name().contains("_LEGGINGS") && !item.getType().name().contains("_BOOTS")
							&& !item.getType().name().contains("_HELMET") && !item.getType().name().contains("_SWORD")
							&& !item.getType().name().contains("_AXE"))
					.collect(Collectors.toList())) {
				entity.getWorld().dropItemNaturally(entity.getLocation(), item);
			}
		}

		event.getDrops().clear();

		respawn(entity, warp);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void respawn(PlayerWarpRespawnEvent event) {
		Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId());

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

	public void respawn(Player player, Warp warp) {
		player.spigot().respawn();
		player.teleport(warp.getSpawnLocation());

		new BukkitRunnable() {

			@Override
			public void run() {
				Bukkit.getPluginManager().callEvent(new PlayerWarpRespawnEvent(player, warp));
			}
		}.runTaskLater(GameMain.getInstance(), 5l);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId());

		GameMain.getInstance().getWarpManager().removeWarp(gamer);
	}

}
