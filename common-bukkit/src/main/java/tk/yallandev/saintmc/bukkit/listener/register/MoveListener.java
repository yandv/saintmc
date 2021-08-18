package tk.yallandev.saintmc.bukkit.listener.register;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.minecraft.server.v1_8_R3.PacketPlayOutPosition;
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
						PlayerMoveUpdateEvent playerMoveUpdateEvent = new PlayerMoveUpdateEvent(player, location,
								player.getLocation());
						Bukkit.getPluginManager().callEvent(playerMoveUpdateEvent);

						if (playerMoveUpdateEvent.isCancelled()) {
							((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutPosition(
									location.getX(),
									location.clone().subtract(0, 0.15, 0).getBlock().getType() == Material.AIR
											? location.getY() - 0.15
											: location.getY(),
									location.getZ(), location.getYaw(), location.getPitch(),
									Collections.<PacketPlayOutPosition.EnumPlayerTeleportFlags>emptySet()));
						}
					}
				}

				locationMap.put(player.getUniqueId(), player.getLocation());
			}
	}

}
