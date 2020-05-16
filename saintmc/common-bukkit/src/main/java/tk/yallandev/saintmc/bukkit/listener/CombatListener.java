package tk.yallandev.saintmc.bukkit.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;

public class CombatListener implements Listener {
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		Player player = (Player) event.getEntity();
		Player damager = null;
		
		if (event.getDamager() instanceof Player) {
			damager = (Player) event.getDamager();
		} else if (event.getDamager() instanceof Projectile) {
			Projectile projectile = (Projectile) event.getDamager();
			
			if (projectile.getShooter() instanceof Player) {
				damager = (Player) projectile.getShooter();
			}
		}
		
		PlayerDamagePlayerEvent playerDamagePlayerEvent = new PlayerDamagePlayerEvent(player, damager, event.isCancelled(), event.getDamage(), event.getFinalDamage());
		
		Bukkit.getPluginManager().callEvent(playerDamagePlayerEvent);
		
		event.setCancelled(playerDamagePlayerEvent.isCancelled());
		event.setDamage(playerDamagePlayerEvent.getDamage());
	}
	
}
