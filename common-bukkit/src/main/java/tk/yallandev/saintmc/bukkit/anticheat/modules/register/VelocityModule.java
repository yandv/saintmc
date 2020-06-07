package tk.yallandev.saintmc.bukkit.anticheat.modules.register;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.anticheat.modules.Module;

public class VelocityModule extends Module {
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getCause() != DamageCause.ENTITY_ATTACK)
			return;
		
		if (!(event.getEntity() instanceof Player))
			return;
		
		Player player = (Player) event.getEntity();
		
		if (player.hasPotionEffect(PotionEffectType.POISON) || player.hasPotionEffect(PotionEffectType.WITHER) || player.getFireTicks() > 0)
			return;
		
		if (FlyModule.hasSurrondingBlocks(player.getLocation().getBlock()))
			return;
		
		Location location = player.getLocation().clone();
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (!player.isOnline())
					return;
				
				if (location.distanceSquared(player.getLocation()) <= 0.001) {
					alert(player);
				}
			}
		}.runTaskLater(BukkitMain.getInstance(), 7l);
	}

}
