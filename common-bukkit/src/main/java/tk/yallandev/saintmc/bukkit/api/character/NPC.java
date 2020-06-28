package tk.yallandev.saintmc.bukkit.api.character;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.WorldServer;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.common.utils.web.WebHelper.Method;

/**
 * 
 * Stenox is the author of the code
 * 
 * @author Stenox
 *
 */

@Getter
public class NPC {

	private EntityPlayer entityPlayer;
	private GameProfile gameProfile;

	private Location location;
	private Property property;

	private Set<UUID> showingSet = new HashSet<>();

	public NPC(Location location, String skin) {
		this.location = location;

		String[] skinProperty = getSkin(skin);
		this.property = new Property("textures", skinProperty[0], skinProperty[1]);
	}

	public void spawn() {
		MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();

		this.gameProfile = new GameProfile(UUID.randomUUID(), "§8[NPC]");
		this.gameProfile.getProperties().put("textures", property);

		this.entityPlayer = new EntityPlayer(minecraftServer, worldServer, gameProfile,
				new PlayerInteractManager(worldServer));

		this.entityPlayer.getBukkitEntity().setRemoveWhenFarAway(false);
		this.entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(),
				location.getPitch());
		Bukkit.getOnlinePlayers().forEach(this::show);
	}

	public void show(Player player) {
		if (showingSet.contains(player.getUniqueId()))
			return;

		showingSet.add(player.getUniqueId());

		PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

		playerConnection.sendPacket(
				new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
		playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
		playerConnection
				.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) (location.getYaw() / 360 * 256)));

		DataWatcher watcher = entityPlayer.getDataWatcher();
		watcher.watch(10, (byte) 127);
		playerConnection.sendPacket(new PacketPlayOutEntityMetadata(entityPlayer.getId(), watcher, true));

		Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitMain.getInstance(), () -> playerConnection.sendPacket(
				new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer)),
				85L);

		try {
			/*
			 * mudei o entityCount do proprio bukkit para public não necessitando mais usar
			 * reflection
			 * 
			 * by stenox
			 */
			int batEntityId = Entity.entityCount++;

			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(player,
						buildSpawnBatPacket(batEntityId, location));

				playerConnection.sendPacket(buildAttachPacket(batEntityId, this.entityPlayer.getId()));
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void hide(Player player) {
		if (!showingSet.contains(player.getUniqueId()))
			return;

		showingSet.remove(player.getUniqueId());
		PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

		playerConnection.sendPacket(
				new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
		playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entityPlayer.getId()));
	}

	public boolean isShowing(UUID uniqueId) {
		return showingSet.contains(uniqueId);
	}

	private static PacketContainer buildSpawnBatPacket(int entityId, Location loc) {
		WrappedDataWatcher watcher = new WrappedDataWatcher();
		watcher.setObject(0, (byte) (1 << 5)); // invisible
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
		packet.getIntegers().write(0, entityId); // id da entidade
		packet.getIntegers().write(1, 65); // bat
		packet.getIntegers().write(2, floor(loc.getX() * 32D));// x
		packet.getIntegers().write(3, floor(loc.getY() * 32D));// y
		packet.getIntegers().write(4, floor(loc.getZ() * 32D));// z
		packet.getDataWatcherModifier().write(0, watcher);

		return packet;
	}

	private static int floor(double var0) {
		int var2 = (int) var0;
		return var0 < (double) var2 ? var2 - 1 : var2;
	}

	private static PacketPlayOutAttachEntity buildAttachPacket(int a, int b) {
		PacketPlayOutAttachEntity packet = new PacketPlayOutAttachEntity();
		setFieldValue(packet, "a", 0);
		setFieldValue(packet, "b", a);
		setFieldValue(packet, "c", b);
		return packet;
	}

	private static void setFieldValue(Object instance, String fieldName, Object value) {
		try {
			Field f = instance.getClass().getDeclaredField(fieldName);
			f.setAccessible(true);
			f.set(instance, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String[] getSkin(String name) {
		try {
			JsonObject jsonObject = CommonConst.DEFAULT_WEB
					.doRequest("https://api.mojang.com/users/profiles/minecraft/" + name, Method.GET).getAsJsonObject();

			String uuid = jsonObject.get("id").getAsString();

			JsonArray jsonArray = CommonConst.DEFAULT_WEB
					.doRequest("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false",
							Method.GET)
					.getAsJsonObject().get("properties").getAsJsonArray();

			JsonObject property = jsonArray.get(0).getAsJsonObject();
			String value = property.get("value").getAsString(), signature = property.get("signature").getAsString();

			return new String[] { value, signature };
		} catch (Exception e) {
			return new String[] { "", "" };
		}
	}
}