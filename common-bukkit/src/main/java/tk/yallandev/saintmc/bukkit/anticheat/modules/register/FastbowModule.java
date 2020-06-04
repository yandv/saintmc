package tk.yallandev.saintmc.bukkit.anticheat.modules.register;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;

import tk.yallandev.saintmc.bukkit.anticheat.modules.Module;

public class FastbowModule extends Module {

	private Map<UUID, Long> normalDelay;

	public FastbowModule() {
		normalDelay = new HashMap<>();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onEntityShootBow(EntityShootBowEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();

		if (event.getForce() == 1.0D) {
			if (normalDelay.containsKey(player.getUniqueId())
					&& normalDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
				alert(player);
			} else {
				normalDelay.put(player.getUniqueId(), System.currentTimeMillis() + 1000L);
			}
		}
	}

}
