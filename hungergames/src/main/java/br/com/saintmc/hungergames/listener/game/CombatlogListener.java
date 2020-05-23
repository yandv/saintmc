package br.com.saintmc.hungergames.listener.game;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;

public class CombatlogListener implements Listener {
	
	private HashMap<UUID, CombatLog> combatMap;
	
	public CombatlogListener() {
		this.combatMap = new HashMap<>();
	}
	
	@EventHandler
	public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
		Player damaged = event.getPlayer();
		Player damager = event.getDamager();
		
		newCombatLog(damaged.getUniqueId(), damager.getUniqueId());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {
		combatMap.remove(event.getEntity().getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		CombatLog log = getCombatLog(p.getUniqueId());
		
		if (log == null)
			return;
		
		if (System.currentTimeMillis() < log.getTime()) {
			Player combatLogger = Bukkit.getPlayer(log.getCombatLogged());
			if (combatLogger != null)
				if (combatLogger.isOnline())
					p.damage(10000.0, combatLogger);
		}
		
		removeCombatLog(p.getUniqueId());
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		if (event.getCause() != DamageCause.VOID)
			return;
		
		Player p = (Player) event.getEntity();
		CombatLog log = getCombatLog(p.getUniqueId());
		
		if (log == null)
			return;
		
		if (System.currentTimeMillis() < log.getTime()) {
			Player combatLogger = Bukkit.getPlayer(log.getCombatLogged());
			if (combatLogger != null)
				if (combatLogger.isOnline())
					p.damage(10000.0, combatLogger);
		}
		
		removeCombatLog(p.getUniqueId());
	}
	
	public CombatLog getCombatLog(UUID uuid) {
		return combatMap.containsKey(uuid) ? combatMap.get(uuid) : null;
	}

	public void newCombatLog(UUID damaged, UUID damager) {
		CombatLog damagedCombatLog = combatMap.get(damaged);
		if (damagedCombatLog == null) {
			damagedCombatLog = combatMap.put(damaged, new CombatLog(damager));
		} else {
			damagedCombatLog.hitted(damager);
		}
		CombatLog damagerCombatLog = combatMap.get(damager);
		if (damagerCombatLog == null) {
			damagerCombatLog = combatMap.put(damager, new CombatLog(damaged));
		} else {
			damagerCombatLog.hitted(damaged);
		}
	}

	public void removeCombatLog(UUID uuid) {
		CombatLog combatLog = combatMap.get(uuid);
		if (combatLog == null)
			return;
		UUID otherPlayer = combatLog.getCombatLogged();
		CombatLog otherPlayerCombatLog = combatMap.get(otherPlayer);
		if (otherPlayerCombatLog != null) {
			if (otherPlayerCombatLog.getCombatLogged() == uuid)
				combatMap.remove(otherPlayer);
		}
		combatMap.remove(uuid);
	}
	
	public class CombatLog {
		
		private UUID combatLogged;
		private long time;

		public CombatLog(UUID combatLogged) {
			this.combatLogged = combatLogged;
			this.time = System.currentTimeMillis() + 10000;
		}

		public UUID getCombatLogged() {
			return combatLogged;
		}

		public long getTime() {
			return time;
		}

		public void hitted(UUID uuid) {
			this.combatLogged = uuid;
			this.time = System.currentTimeMillis() + 10000;
		}
	}

}
