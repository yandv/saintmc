package tk.yallandev.saintmc.lobby.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import tk.yallandev.saintmc.bukkit.event.PlayerMoveUpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.lobby.LobbyMain;
import tk.yallandev.saintmc.lobby.collectable.Point3D;
import tk.yallandev.saintmc.lobby.gamer.Gamer;

public class ParticleListener implements Listener {

	private Point3D[] outline = { new Point3D(0, 0, -0.5f), new Point3D(0.1f, 0.01f, -0.5f),
			new Point3D(0.3f, 0.03f, -0.5f), new Point3D(0.4f, 0.04f, -0.5f), new Point3D(0.6f, 0.1f, -0.5f),
			new Point3D(0.61f, 0.2f, -0.5f), new Point3D(0.62f, 0.4f, -0.5f), new Point3D(0.63f, 0.6f, -0.5f),
			new Point3D(0.635f, 0.7f, -0.5f), new Point3D(0.7f, 0.7f, -0.5f), new Point3D(0.9f, 0.75f, -0.5f),
			new Point3D(1.2f, 0.8f, -0.5f), new Point3D(1.4f, 0.9f, -0.5f), new Point3D(1.6f, 1f, -0.5f),
			new Point3D(1.8f, 1.1f, -0.5f), new Point3D(1.85f, 0.9f, -0.5f), new Point3D(1.9f, 0.7f, -0.5f),
			new Point3D(1.85f, 0.5f, -0.5f), new Point3D(1.8f, 0.3f, -0.5f), new Point3D(1.75f, 0.1f, -0.5f),
			new Point3D(1.7f, -0.1f, -0.5f), new Point3D(1.65f, -0.3f, -0.5f), new Point3D(1.55f, -0.5f, -0.5f),
			new Point3D(1.45f, -0.7f, -0.5f), new Point3D(1.30f, -0.75f, -0.5f), new Point3D(1.15f, -0.8f, -0.5f),
			new Point3D(1.0f, -0.85f, -0.5f), new Point3D(0.8f, -0.87f, -0.5f), new Point3D(0.6f, -0.7f, -0.5f),
			new Point3D(0.5f, -0.5f, -0.5f), new Point3D(0.4f, -0.3f, -0.5f), new Point3D(0.3f, -0.3f, -0.5f),
			new Point3D(0.15f, -0.3f, -0.5f), new Point3D(0f, -0.3f, -0.5f),

			//
			new Point3D(0.9f, 0.55f, -0.5f), new Point3D(1.2f, 0.6f, -0.5f), new Point3D(1.4f, 0.7f, -0.5f),
			new Point3D(1.6f, 0.9f, -0.5f),
			//
			new Point3D(0.9f, 0.35f, -0.5f), new Point3D(1.2f, 0.4f, -0.5f), new Point3D(1.4f, 0.5f, -0.5f),
			new Point3D(1.6f, 0.7f, -0.5f),
			//
			new Point3D(0.9f, 0.15f, -0.5f), new Point3D(1.2f, 0.2f, -0.5f), new Point3D(1.4f, 0.3f, -0.5f),
			new Point3D(1.6f, 0.5f, -0.5f),
			//
			new Point3D(0.9f, -0.05f, -0.5f), new Point3D(1.2f, 0f, -0.5f), new Point3D(1.4f, 0.1f, -0.5f),
			new Point3D(1.6f, 0.3f, -0.5f),
			//
			new Point3D(0.7f, -0.25f, -0.5f), new Point3D(1.0f, -0.2f, -0.5f), new Point3D(1.2f, -0.1f, -0.5f),
			new Point3D(1.4f, 0.1f, -0.5f),
			//
			new Point3D(0.7f, -0.45f, -0.5f), new Point3D(1.0f, -0.4f, -0.5f), new Point3D(1.2f, -0.3f, -0.5f),
			new Point3D(1.4f, -0.1f, -0.5f),
			//
			new Point3D(1.30f, -0.55f, -0.5f), new Point3D(1.15f, -0.6f, -0.5f), new Point3D(1.0f, -0.65f, -0.5f) };

	private Point3D[] fill = { new Point3D(1.2f, 0.6f, -0.5f), new Point3D(1.4f, 0.7f, -0.5f),

			new Point3D(1.1f, 0.2f, -0.5f), new Point3D(1.3f, 0.3f, -0.5f),

			new Point3D(1.0f, -0.2f, -0.5f), new Point3D(1.2f, -0.1f, -0.5f), };

	private Map<UUID, Long> map;

	public ParticleListener() {
		map = new HashMap<>();
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		for (Gamer gamer : LobbyMain.getInstance().getPlayerManager().getGamers().stream().filter(
				gamer -> !LobbyMain.getInstance().getPlayerManager().getPlayersInCombat().contains(gamer.getPlayer()))
				.collect(Collectors.toList())) {

			if (gamer.isUsingWing()) {
				
				if (event.getCurrentTick() % 5 == 0)
					if (map.containsKey(gamer.getPlayer().getUniqueId()) && map.get(gamer.getPlayer().getUniqueId()) < System.currentTimeMillis()
							|| !map.containsKey(gamer.getPlayer().getUniqueId())) {

						Player player = gamer.getPlayer();
						EnumParticle particle = gamer.getWingParticle();

						Location playerLocation = player.getEyeLocation();
						float x = (float) playerLocation.getX();
						float y = (float) playerLocation.getY() - 0.2f;
						float z = (float) playerLocation.getZ();
						float rot = -playerLocation.getYaw() * 0.017453292F;

						Point3D rotated = null;
						for (Point3D point : outline) {
							rotated = point.rotate(rot);

							PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true,
									rotated.x + x, rotated.y + y, rotated.z + z, 0, 0, 0, 0, 1);

							point.z *= -1;
							rotated = point.rotate(rot + 3.1415f);
							point.z *= -1;

							PacketPlayOutWorldParticles packet2 = new PacketPlayOutWorldParticles(particle, true,
									rotated.x + x, rotated.y + y, rotated.z + z, 0, 0, 0, 0, 1);
							for (Player online : Bukkit.getOnlinePlayers()) {
								if (online.canSee(player)) {
									((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
									((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet2);
								}
							}
						}

						for (Point3D point : fill) {
							rotated = point.rotate(rot);

							PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true,
									rotated.x + x, rotated.y + y, rotated.z + z, 0, 0, 0, 0, 1);

							point.z *= -1;
							rotated = point.rotate(rot + 3.1415f);
							point.z *= -1;

							PacketPlayOutWorldParticles packet2 = new PacketPlayOutWorldParticles(particle, true,
									rotated.x + x, rotated.y + y, rotated.z + z, 0, 0, 0, 0, 1);
							for (Player online : Bukkit.getOnlinePlayers()) {
								if (online.canSee(player)) {
									((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
									((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet2);
								}
							}
						}
					}
				
				continue;
			}

			if (gamer.isUsingParticle()) {
				Player player = gamer.getPlayer();
				gamer.setAlpha(gamer.getAlpha() + Math.PI / 16);

				double alpha = gamer.getAlpha();

				Location loc = player.getLocation();
				Location firstLocation = loc.clone().add(Math.cos(alpha), Math.sin(alpha) + 1, Math.sin(alpha));
				Location secondLocation = loc.clone().add(Math.cos(alpha + Math.PI), Math.sin(alpha) + 1,
						Math.sin(alpha + Math.PI));

				PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(gamer.getParticle(), true,
						(float) firstLocation.getX(), (float) firstLocation.getY(), (float) firstLocation.getZ(), 0, 0,
						0, 0, 1);
				PacketPlayOutWorldParticles packet2 = new PacketPlayOutWorldParticles(gamer.getParticle(), true,
						(float) secondLocation.getX(), (float) secondLocation.getY(), (float) secondLocation.getZ(), 0,
						0, 0, 0, 1);

				for (Player online : Bukkit.getOnlinePlayers()) {
					if (online.canSee(player)) {
						((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
						((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet2);
					}
				}

				continue;
			}
		}
	}

	@EventHandler
	public void onPlayerMoveUpdate(PlayerMoveUpdateEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = LobbyMain.getInstance().getPlayerManager().getGamer(player);

		if (gamer.isUsingWing())
			map.put(player.getUniqueId(), System.currentTimeMillis() + 250);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		map.remove(event.getPlayer().getUniqueId());
	}

}
