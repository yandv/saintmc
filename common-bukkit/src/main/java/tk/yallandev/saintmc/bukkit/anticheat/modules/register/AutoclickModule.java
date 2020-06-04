package tk.yallandev.saintmc.bukkit.anticheat.modules.register;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import tk.yallandev.saintmc.bukkit.anticheat.modules.Clicks;
import tk.yallandev.saintmc.bukkit.anticheat.modules.Module;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;

public class AutoclickModule extends Module {
	
	private Map<Player, Clicks> clicksPerSecond;
	
	public AutoclickModule() {
		clicksPerSecond = new HashMap<>();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_AIR)
			return;
		
		Player player = event.getPlayer();
		Clicks click = clicksPerSecond.computeIfAbsent(player, v -> new Clicks());
		
		if (click.getExpireTime() < System.currentTimeMillis()) {
			if (click.getClicks() >= 16) {
				alert(player, click.getClicks());
			}
			
			clicksPerSecond.remove(player);
			return;
		}
		
		click.addClick();
	}
	
	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND) {
			Iterator<Entry<Player, Clicks>> iterator = clicksPerSecond.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<Player, Clicks> entry = iterator.next();

				if (entry.getValue().getExpireTime() < System.currentTimeMillis()) {
					if (entry.getValue().getClicks() >= 16) {
						alert(entry.getKey(), entry.getValue().getClicks());
					}
					
					iterator.remove();
				}
			}
		}
	}
	
}
