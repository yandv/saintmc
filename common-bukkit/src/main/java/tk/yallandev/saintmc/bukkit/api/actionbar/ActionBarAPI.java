package tk.yallandev.saintmc.bukkit.api.actionbar;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.protocol.ProtocolGetter;
import tk.yallandev.saintmc.bukkit.api.protocol.ProtocolVersion;

public class ActionBarAPI {

	public static void send(Player player, String text) {
		ProtocolVersion version = ProtocolGetter.getVersion(player);

		if (version.getId() >= ProtocolVersion.MINECRAFT_1_8.getId()) {
			PacketContainer chatPacket = new PacketContainer(PacketType.Play.Server.CHAT);
			chatPacket.getChatComponents().write(0, WrappedChatComponent.fromJson("{\"text\":\"" + text + " \"}"));
			chatPacket.getBytes().write(0, (byte) 2);

			try {
				BukkitMain.getInstance().getProcotolManager().sendServerPacket(player, chatPacket);
			} catch (InvocationTargetException e) {
				throw new RuntimeException("Cannot send packet " + chatPacket, e);
			}
		}
	}

	public static void broadcast(String text) {
		Bukkit.getOnlinePlayers().forEach(p -> send(p, text));
	}

}
