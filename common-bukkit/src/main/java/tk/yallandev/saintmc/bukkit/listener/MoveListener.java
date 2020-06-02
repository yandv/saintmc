package tk.yallandev.saintmc.bukkit.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import tk.yallandev.saintmc.bukkit.event.PlayerMoveUpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;

public class MoveListener implements Listener {

	private Map<UUID, Location> locationMap;

	public MoveListener() {
		locationMap = new HashMap<>();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onUpdate(UpdateEvent event) {
		if (event.getCurrentTick() % 5 == 0)
			for (Player player : Bukkit.getOnlinePlayers()) {

				if (locationMap.containsKey(player.getUniqueId())) {
					Location location = locationMap.get(player.getUniqueId());

					if (location.getX() != player.getLocation().getX() || location.getZ() != player.getLocation().getZ()
							|| location.getY() != player.getLocation().getY()) {
						PlayerMoveUpdateEvent realMoveEvent = new PlayerMoveUpdateEvent(player, location,
								player.getLocation());
						Bukkit.getPluginManager().callEvent(realMoveEvent);

						if (realMoveEvent.isCancelled())
							player.teleport(location);
					}
				}

				locationMap.put(player.getUniqueId(), player.getLocation());
			}
	}

}