package tk.yallandev.saintmc.bukkit.api.hologram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.EntityHorse;
import net.minecraft.server.v1_8_R3.EntityWitherSkull;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.World;
import tk.yallandev.saintmc.bukkit.protocol.ProtocolGetter;

public class BasicHologram implements Hologram {

	private String displayName;
	private Location location;

	private boolean registred;

	private List<Hologram> lineList = new ArrayList<>();
	private List<Player> viewerList = new ArrayList<>();

	private Map<UUID, Long> lockMap = new HashMap<>();

	private double linesSpace;

	/*
	 * 1.7 hologram
	 */

	private EntityHorse entityHorse;
	private EntityWitherSkull entitySkull;

	private PacketPlayOutSpawnEntity spawnSkull;
	private PacketPlayOutSpawnEntityLiving spawnHorse;

	private PacketPlayOutAttachEntity attachEntity;

	private PacketPlayOutEntityDestroy destroyHorse;

	/*
	 * 1.8 hologram
	 */

	private EntityArmorStand entityStand;

	private PacketPlayOutSpawnEntityLiving spawnStand;
	private PacketPlayOutEntityDestroy destroyStand;

	public BasicHologram(String displayName, Location location, boolean registred) {
		this.displayName = displayName;
		this.location = location;

		World world = ((CraftWorld) location.getWorld()).getHandle();

		/*
		 * 1.8
		 */

		entityStand = new EntityArmorStand(world);
		entityStand.setLocation(location.getX(), location.getY() - 1, location.getZ(), 0, 0);
		entityStand.setCustomName(displayName);
		entityStand.setCustomNameVisible(!displayName.isEmpty());

		entityStand.setInvisible(true);
		entityStand.setGravity(false);

		spawnStand = new PacketPlayOutSpawnEntityLiving(entityStand);
		destroyStand = new PacketPlayOutEntityDestroy(entityStand.getId());

		/*
		 * 1.7
		 */

		entitySkull = new EntityWitherSkull(world);
		entitySkull.setLocation(location.getX(), location.getY() + 56.28, location.getZ(), 0, 0);

		spawnSkull = new PacketPlayOutSpawnEntity(entitySkull, 66);

		entityHorse = new EntityHorse(world);
		entityHorse.setCustomName(displayName);
		entityHorse.setCustomNameVisible(!displayName.isEmpty());
		entityHorse.setAge(-1700000);
		entityHorse.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);

		spawnHorse = new PacketPlayOutSpawnEntityLiving(entityHorse);

		attachEntity = new PacketPlayOutAttachEntity(0, entityHorse, entitySkull);
		destroyHorse = new PacketPlayOutEntityDestroy(entityHorse.getId(), entitySkull.getId());
		this.registred = registred;
		HologramListener.getHolograms().add(this);
	}

	public BasicHologram(String displayName, Location location) {
		this(displayName, location, true);
	}

	@Override
	@Deprecated
	public void spawn() {

	}

	@Override
	public void setDisplayName(String displayName) {
		this.displayName = displayName;

		entityStand.setCustomName(displayName);
		entityStand.setCustomNameVisible(!displayName.isEmpty());

		entityHorse.setCustomName(displayName);
		entityHorse.setCustomNameVisible(!displayName.isEmpty());

		for (Player viewer : viewerList) {
			if (ProtocolGetter.getVersion(viewer).getId() >= 47) {
				sendPacket(viewer, spawnStand);
				PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(entityStand.getId(),
						entityStand.getDataWatcher(), true);
				sendPacket(viewer, metadata);
			} else {
				sendPacket(viewer, spawnHorse, spawnSkull, attachEntity);
				PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(entityHorse.getId(),
						entityHorse.getDataWatcher(), true);
				sendPacket(viewer, metadata);
			}
		}
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public void teleport(Location location) {
		entitySkull.setLocation(location.getX(), location.getY() + 56.28, location.getZ(), location.getYaw(),
				location.getPitch());
		entityStand.setLocation(location.getX(), location.getY() - 1, location.getZ(), location.getYaw(),
				location.getPitch());

		PacketPlayOutEntityTeleport teleportSkull = new PacketPlayOutEntityTeleport(entitySkull);
		PacketPlayOutEntityTeleport teleportStand = new PacketPlayOutEntityTeleport(entityStand);

		for (Player viewer : viewerList) {
			if (ProtocolGetter.getVersion((CraftPlayer) viewer).getId() >= 47) {
				sendPacket(viewer, teleportStand);
			} else {
				sendPacket(viewer, teleportSkull);
			}
		}

	}

	@Override
	public void destroy() {
		for (Player viewer : viewerList) {
			if (ProtocolGetter.getVersion(viewer).getId() >= 47) {
				sendPacket(viewer, destroyStand);
			} else {
				sendPacket(viewer, destroyHorse);
			}
		}
		
		for (Hologram line : lineList)
			line.destroy();

		viewerList.clear();
		entityHorse.die();
		entitySkull.die();
		entityStand.die();
		HologramListener.getHolograms().remove(this);
	}

	@Override
	public void addViewer(Player player) {
		if (isViewer(player))
			return;

		if (ProtocolGetter.getVersion(player).getId() >= 47) {
			sendPacket(player, spawnStand);
			sendPacket(player,
					new PacketPlayOutEntityMetadata(entityStand.getId(), entityStand.getDataWatcher(), true));
		} else {
			sendPacket(player, spawnSkull);
			sendPacket(player, spawnHorse);
			sendPacket(player, attachEntity);
			sendPacket(player, new PacketPlayOutEntityMetadata(entityHorse.getId(),
					entityHorse.getDataWatcher(), true));
		}

		viewerList.add(player);

		for (Hologram line : lineList) {
			line.addViewer(player);
		}
	}

	@Override
	public void removeViewer(Player player) {
		if (!isViewer(player))
			return;

		if (ProtocolGetter.getVersion(player).getId() >= 47) {
			sendPacket(player, destroyStand);
		} else {
			sendPacket(player, destroyHorse);
		}

		viewerList.remove(player);

		for (Hologram line : lineList) {
			line.removeViewer(player);
		}
	}

	@Override
	public boolean locked(Player player) {
		return lockMap.containsKey(player.getUniqueId())
				&& lockMap.get(player.getUniqueId()) > System.currentTimeMillis();
	}

	@Override
	public void lock(Player player, long time) {
		lockMap.put(player.getUniqueId(), System.currentTimeMillis() + time);
	}

	@Override
	public boolean isViewer(Player player) {
		return viewerList.contains(player);
	}

	@Override
	public List<Player> getViewerList() {
		return viewerList;
	}

	@Override
	public Hologram addLine(String displayName) {
		linesSpace -= 0.25;
		Hologram hologram = new BasicHologram(displayName, getLocation().clone().add(0, linesSpace, 0), registred);
		lineList.add(hologram);
		return hologram;
	}

	@Override
	public List<Hologram> getLineList() {
		return lineList;
	}

	@Override
	public boolean isRegistred() {
		return registred;
	}

	public void sendPacket(Player player, Packet<?>... packetList) {
		for (Packet<?> packet : packetList) {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		}
	}

}
