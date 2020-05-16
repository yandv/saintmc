package tk.yallandev.saintmc.gameapi.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.gameapi.GameMain;

public class SpectatorListener implements Listener {

	private boolean isSpectator(Player player) {
		Gamer gamer = GameMain.getPlugin().getGamerManager().getGamer(player.getUniqueId());
		return gamer.isSpectator();
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		//TODO FAZER AQ TBM PORRA FDP
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onRegen(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player)
			if (isSpectator((Player) event.getEntity()))
				event.setCancelled(true);
	}

	@EventHandler
	public void onExpChange(PlayerExpChangeEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setAmount(0);
	}

	@EventHandler
	public void onMobTarget(EntityTargetEvent event) {
		if (event.getTarget() instanceof Player)
			if (isSpectator((Player) event.getTarget()))
				event.setCancelled(true);
	}

	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPickupItem(PlayerPickupItemEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player)
			if (isSpectator((Player) event.getEntity()))
				event.setCancelled(true);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player)
			if (Gamer.getGamer((Player) event.getEntity()).isSpectator())
				event.setCancelled(true);
		
		if (event.getDamager() instanceof Player)
			if (Gamer.getGamer((Player) event.getDamager()).isSpectator())
				event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player)
			if (isSpectator((Player) event.getEntity()))
				event.setCancelled(true);
	}

	@EventHandler
	protected void onBlockCanBuild(BlockCanBuildEvent e) {
		if (!e.isBuildable()) {
			Location blockL = e.getBlock().getLocation();
			boolean allowed = false;
			
			for (Player target : Bukkit.getOnlinePlayers()) {
				Gamer targetGamer = Gamer.getGamer(target);
				
				if (!targetGamer.isNotPlaying())
					continue;
				
				if (target.getWorld().equals(e.getBlock().getWorld())) {
					Location playerL = target.getLocation();
					if (playerL.getX() > blockL.getBlockX() - 1 && playerL.getX() < blockL.getBlockX() + 1) {
						if (playerL.getZ() > blockL.getBlockZ() - 1 && playerL.getZ() < blockL.getBlockZ() + 1) {
							if (playerL.getY() > blockL.getBlockY() - 1 && playerL.getY() < blockL.getBlockY() + 2) {
								allowed = true;
								break;
							}
						}
					}

				}
			}
			e.setBuildable(allowed);
		}
	}

}
