package tk.yallandev.saintmc.bukkit.api.protocol;

import static tk.yallandev.saintmc.bukkit.api.protocol.ProtocolVersion.MINECRAFT_1_10;
import static tk.yallandev.saintmc.bukkit.api.protocol.ProtocolVersion.MINECRAFT_1_11;
import static tk.yallandev.saintmc.bukkit.api.protocol.ProtocolVersion.MINECRAFT_1_7_10;
import static tk.yallandev.saintmc.bukkit.api.protocol.ProtocolVersion.MINECRAFT_1_7_5;
import static tk.yallandev.saintmc.bukkit.api.protocol.ProtocolVersion.MINECRAFT_1_8;
import static tk.yallandev.saintmc.bukkit.api.protocol.ProtocolVersion.MINECRAFT_1_9;
import static tk.yallandev.saintmc.bukkit.api.protocol.ProtocolVersion.UNKNOWN;

import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.BukkitMain;

public class ProtocolGetter {

	@Getter
	private static boolean viaVersion, protocolSupport, protocolHack;
	private static ProtocolVersion nativeVersion = UNKNOWN;

	public static void foundDependencies() {
		try {
			Class.forName("protocolsupport.api.ProtocolSupportAPI");
			BukkitMain.getInstance().getLogger().info("ProtocolSupport encontrado!");
			protocolSupport = true;
		} catch (ClassNotFoundException e) {
//			if (version.equals("v1_7_R4")) {
//				try {
//					Class.forName("org.spigotmc.ProtocolInjector");
//					BukkitMain.getInstance().getLogger().info("ProtocolHack encontrado!");
//					protocolHack = true;
//				} catch (ClassNotFoundException e2) {
//				}
//			}
		}

		try {
			Class.forName("us.myles.ViaVersion.api.Via");
			BukkitMain.getInstance().getLogger().info("ViaVersion encontrado!");
			viaVersion = true;
		} catch (ClassNotFoundException e) {
		}
		
		try {
			String version = Bukkit.getServer().getClass().getPackage().getName().substring(23);

			if (version.startsWith("v1_7_R")) {
				nativeVersion = (version.endsWith("R1") || version.endsWith("R2")) ? MINECRAFT_1_7_5 : MINECRAFT_1_7_10;
			} else if (version.startsWith("v1_8_R")) {
				nativeVersion = MINECRAFT_1_8;
			} else if (version.startsWith("v1_9_R")) {
				nativeVersion = MINECRAFT_1_9;
			} else if (version.startsWith("v1_10_R")) {
				nativeVersion = MINECRAFT_1_10;
			} else if (version.startsWith("v1_11_R")) {
				nativeVersion = MINECRAFT_1_11;
			}
		} catch (StringIndexOutOfBoundsException ex) {
			nativeVersion = MINECRAFT_1_8;
		}
	}

	public static ProtocolVersion getVersion(Player player) {
		try {
			if (viaVersion) {
				Class<?> clazz = Class.forName("us.myles.ViaVersion.api.Via");
				Object object = clazz.getDeclaredMethod("getAPI").invoke(null);
				Method method = object.getClass().getMethod("getPlayerVersion", UUID.class);
				return ProtocolVersion.getById((int) method.invoke(object, player.getUniqueId()));
			} else if (protocolSupport) {
				Class<?> clazz = Class.forName("protocolsupport.api.ProtocolSupportAPI");
				Method method = clazz.getDeclaredMethod("getProtocolVersion", Player.class);
				return ProtocolVersion.valueOf((String) method.invoke(null, player));
			} else if (protocolHack) {
				Object handle = player.getClass().getMethod("getHandle").invoke(player);
				Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
				Object networkManager = playerConnection.getClass().getField("networkManager").get(playerConnection);
				return ProtocolVersion
						.getById((int) networkManager.getClass().getMethod("getVersion").invoke(networkManager));
			} else {
				return nativeVersion;
			}
		} catch (Exception e) {
		}

		return ProtocolVersion.UNKNOWN;
	}
	
	public static int getPing(Player player) {
		return ((CraftPlayer)player).getHandle().ping;
	}
}
