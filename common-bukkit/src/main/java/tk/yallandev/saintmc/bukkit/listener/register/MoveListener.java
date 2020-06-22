package tk.yallandev.saintmc.bukkit.listener.register;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import tk.yallandev.saintmc.bukkit.event.PlayerMoveUpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.listener.Listener;

public class MoveListener extends Listener {

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
							if (location.clone().subtract(0, 0.15, 0).getBlock().getType() == Material.AIR)
								player.teleport(location.subtract(0, 0.15, 0));
							else
								player.teleport(location);
					}
				}

				locationMap.put(player.getUniqueId(), player.getLocation());
			}
	}

}
