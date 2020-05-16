package tk.yallandev.saintmc.kitpvp.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerQuitEvent;

import tk.yallandev.saintmc.bukkit.event.PlayerMoveUpdateEvent;

public class LauncherListener implements Listener {
	
	private Map<Player, Long> fallMap;
	
	public LauncherListener() {
		fallMap = new HashMap<>();
	}
	
	@EventHandler
	public void onRealMove(PlayerMoveUpdateEvent event) {
		Player player = event.getPlayer();
		Material type = event.getTo().getBlock().getRelative(BlockFace.DOWN).getType();
		
		boolean noFall = false; 
		
		if (type == Material.DIAMOND_BLOCK) {
			player.setVelocity(player.getLocation().getDirection().multiply(0).setY(2.5));
			noFall = true;
		} else if (type == Material.SPONGE) {
			player.setVelocity(player.getLocation().getDirection().multiply(0).setY(4));
			noFall = true;
		}
		
		if (noFall) {
			player.playSound(player.getLocation(), Sound.LEVEL_UP, 6.0F, 1.0F);
			fallMap.put(player, System.currentTimeMillis() + 7000l);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (fallMap.containsKey(event.getPlayer()))
			fallMap.remove(event.getPlayer());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getCause() != DamageCause.FALL)
			return;
		
		if (!(event.getEntity() instanceof Player))
			return;
		
		Player player = (Player) event.getEntity();
		
		if (fallMap.containsKey(player))
			if (fallMap.get(player) > System.currentTimeMillis()) {
				event.setCancelled(true);
				fallMap.remove(player);
			}
	}

}
