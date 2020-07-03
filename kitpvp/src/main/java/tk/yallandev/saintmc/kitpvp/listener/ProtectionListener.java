package tk.yallandev.saintmc.kitpvp.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.event.PlayerMoveUpdateEvent;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerLostProtectionEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpJoinEvent;
import tk.yallandev.saintmc.kitpvp.gamer.Gamer;
import tk.yallandev.saintmc.kitpvp.warp.Warp;

public class ProtectionListener implements Listener {

	@EventHandler
	public void onPlayerWarpJoin(PlayerWarpJoinEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId());

		gamer.setSpawnProtection(true);

		if (event.getWarp().getWarpSettings().isSpawnProtection())
			player.sendMessage("§a§l> §fVocê §arecebeu§f a proteção do spawn!");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();
		Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId());

		if (!gamer.getWarp().getWarpSettings().isDamageEnabled()) {
			event.setCancelled(true);
			return;
		}

		if (gamer.getWarp().getWarpSettings().isSpawnProtection() && gamer.isSpawnProtection())
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDamage(PlayerDamagePlayerEvent event) {
		Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId());

		if (!gamer.getWarp().getWarpSettings().isPvpEnabled()) {
			event.setCancelled(true);
			return;
		}
		
		if (gamer.getWarp().getWarpSettings().isSpawnProtection() && (gamer.isSpawnProtection() || GameMain
				.getInstance().getGamerManager().getGamer(event.getDamager().getUniqueId()).isSpawnProtection()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onRealMove(PlayerMoveUpdateEvent event) {
		Player player = event.getPlayer();

		if (AdminMode.getInstance().isAdmin(player))
			return;

		Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId());

		if (!gamer.isSpawnProtection())
			return;

		Warp warp = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId()).getWarp();

		if (!warp.getWarpSettings().isSpawnProtection())
			return;

		Location to = event.getTo();
		double distX = to.getX() - warp.getSpawnLocation().getX();
		double distZ = to.getZ() - warp.getSpawnLocation().getZ();

		double distance = (distX * distX) + (distZ * distZ);
		double spawnRadius = warp.getSpawnRadius() * warp.getSpawnRadius();

		if (distance > spawnRadius) {
			gamer.setSpawnProtection(false);
			player.sendMessage("§a§l> §fVocê §8perdeu§f a proteção do spawn!");
			Bukkit.getPluginManager().callEvent(new PlayerLostProtectionEvent(player, warp));
		}
	}

}
