package tk.yallandev.saintmc.game.games.hungergames.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import tk.yallandev.saintmc.bukkit.api.actionbar.ActionBarAPI;
import tk.yallandev.saintmc.common.utils.string.NameUtils;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.constructor.CombatLog;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.games.hungergames.manager.CombatLogManager;
import tk.yallandev.saintmc.game.stage.GameStage;

public class CombatLogListener implements Listener {
	private CombatLogManager manager;

	public CombatLogListener() {
		this.manager = new CombatLogManager();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		if (!(event.getDamager() instanceof Player))
			return;
		
		Player damager = (Player) event.getDamager();
		Player damaged = (Player) event.getEntity();
		this.manager.newCombatLog(damaged.getUniqueId(), damager.getUniqueId());
		
		if (GameMain.getPlugin().getGameStage() == GameStage.GAMETIME) {
			String damagerKit = NameUtils.formatString(Gamer.getGamer(damager).getKitName());
			String damagedKit = NameUtils.formatString(Gamer.getGamer(damaged).getKitName());
			
			ActionBarAPI.send(damaged, "§a" + damager.getName() + " - " + damagerKit);
			ActionBarAPI.send(damager, "§a" + damaged.getName() + " - " + damagedKit);
//			BossBarAPI.setBar(damager, T.t(BukkitMain.getInstance(), BattlePlayer.getLanguage(damager.getUniqueId()), "player-ability").replace("%player%", damaged.getName()).replace("%kit%", damagedKit), 5);
//			BossBarAPI.setBar(damaged, T.t(BukkitMain.getInstance(), BattlePlayer.getLanguage(damaged.getUniqueId()), "player-ability").replace("%player%", damager.getName()).replace("%kit%", damagerKit), 5);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		this.manager.removeCombatLog(p.getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		CombatLog log = this.manager.getCombatLog(p.getUniqueId());
		
		if (log == null)
			return;
		
		if (System.currentTimeMillis() < log.getTime()) {
			Player combatLogger = Bukkit.getPlayer(log.getCombatLogged());
			if (combatLogger != null)
				if (combatLogger.isOnline())
					p.damage(10000.0, combatLogger);
		}
		
		this.manager.removeCombatLog(p.getUniqueId());
	}

	@EventHandler
	public void onVoidDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		if (event.getCause() != DamageCause.VOID)
			return;
		
		Player p = (Player) event.getEntity();
		CombatLog log = this.manager.getCombatLog(p.getUniqueId());
		
		if (log == null)
			return;
		
		if (System.currentTimeMillis() < log.getTime()) {
			Player combatLogger = Bukkit.getPlayer(log.getCombatLogged());
			if (combatLogger != null)
				if (combatLogger.isOnline())
					p.damage(10000.0, combatLogger);
		}
		
		this.manager.removeCombatLog(p.getUniqueId());
	}

}
