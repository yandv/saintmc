package br.com.saintmc.hungergames.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.gamer.Gamer;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.event.admin.PlayerAdminModeEvent;
import tk.yallandev.saintmc.bukkit.event.teleport.PlayerTeleportCommandEvent;
import tk.yallandev.saintmc.bukkit.event.teleport.PlayerTeleportCommandEvent.TeleportResult;
import tk.yallandev.saintmc.bukkit.event.vanish.PlayerHideToPlayerEvent;
import tk.yallandev.saintmc.bukkit.event.vanish.PlayerShowToPlayerEvent;
import tk.yallandev.saintmc.common.account.Member;

public class SpectatorListener extends GameListener {

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		//TODO FAZER AQ TBM PORRA FDP
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player)
			if (isSpectator((Player) event.getEntity()))
				event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerExpChange(PlayerExpChangeEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setAmount(0);
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getTarget() instanceof Player)
			if (isSpectator((Player) event.getTarget()))
				event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
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
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player)
			if (isSpectator((Player) event.getEntity()))
				event.setCancelled(true);
		
		if (event.getDamager() instanceof Player)
			if (isSpectator((Player) event.getDamager()))
				event.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
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
				Gamer targetGamer = GameGeneral.getInstance().getGamerController().getGamer(target);
				
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
	
	@EventHandler
	public void onPlayerAdminMode(PlayerAdminModeEvent event) {
		if (isPregame()) {
			Gamer gamer = getGameGeneral().getGamerController().getGamer(event.getPlayer().getUniqueId());

			if (event.getAdminMode() == PlayerAdminModeEvent.AdminMode.ADMIN) {
				gamer.setGamemaker(true);
			} else {
				gamer.setGamemaker(false);
				gamer.setSpectator(false);
			}
		} else {
			Gamer gamer = getGameGeneral().getGamerController().getGamer(event.getPlayer().getUniqueId());

			if (event.getAdminMode() == PlayerAdminModeEvent.AdminMode.ADMIN) {
				gamer.setGamemaker(true);
			} else {
				gamer.setGamemaker(false);
				gamer.setSpectator(false);
			}
		}
	}
	
	@EventHandler
	public void onVisible(PlayerShowToPlayerEvent event) {
		if (!isSpectator(event.getPlayer()))
			return;
		
		Gamer toGamer = GameGeneral.getInstance().getGamerController().getGamer(event.getToPlayer());
		
		if (!toGamer.isNotPlaying()) {
			if (!toGamer.isSpectatorsEnabled())
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerShowToPlayer(PlayerShowToPlayerEvent event) {
		Gamer toGamer = GameGeneral.getInstance().getGamerController().getGamer(event.getToPlayer());
		
		if (AdminMode.getInstance().isAdmin(event.getPlayer()))
			if (!toGamer.isSpectatorsEnabled() && Member.getGroup(event.getPlayer().getUniqueId()).ordinal() - 1 > Member.getGroup(event.getToPlayer().getUniqueId()).ordinal()) {
				event.setCancelled(true);
			}
	}
	
	@EventHandler
	public void onPlayerHideToPlayer(PlayerHideToPlayerEvent event) {
		Gamer toGamer = GameGeneral.getInstance().getGamerController().getGamer(event.getToPlayer());
		
		if (AdminMode.getInstance().isAdmin(event.getPlayer()))
			if (toGamer.isSpectatorsEnabled() && Member.getGroup(event.getPlayer().getUniqueId()).ordinal() - 1 <= Member.getGroup(event.getToPlayer().getUniqueId()).ordinal())  {
				event.setCancelled(true);
			}
	}
	
	@EventHandler
	public void onPlayerTeleportCommand(PlayerTeleportCommandEvent event) {
		if (event.getResult() != TeleportResult.NO_PERMISSION)
			return;
		
		if (GameGeneral.getInstance().getGamerController().getGamer(event.getPlayer()).isSpectator())
			event.setResult(TeleportResult.ONLY_PLAYER_TELEPORT);
	}
	
	private boolean isSpectator(Player player) {
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player.getUniqueId());
		return gamer.isSpectator();
	}

}
