package tk.yallandev.saintmc.bukkit.api.title.types;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.EnumWrappers.TitleAction;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.packet.PacketBuilder;
import tk.yallandev.saintmc.bukkit.api.title.Title;

@Getter
public class SimpleTitle implements Title {

	private String title, subtitle;
	private int fadeInTime, stayTime, fadeOutTime;

	public SimpleTitle(String title, String subtitle) {
		this.title = title == null ? " " : title;
		this.subtitle = subtitle == null ? " " : subtitle;

		this.fadeInTime = 10;
		this.stayTime = 20;
		this.fadeInTime = 10;
	}

	public SimpleTitle(String title, String subtitle, int fadeInTime, int stayTime, int fadeOutTime) {
		this.title = title == null ? " " : title;
		this.subtitle = subtitle == null ? " " : subtitle;

		this.fadeInTime = fadeInTime;
		this.stayTime = stayTime;
		this.fadeInTime = fadeOutTime;
	}

	@Override
	public void send(Player player) {
		sendPacket(player,
				new PacketBuilder(PacketType.Play.Server.TITLE).writeTitleAction(0, TitleAction.RESET)
				.writeInteger(0, fadeInTime)
				.writeInteger(1, stayTime)
				.writeInteger(2, fadeOutTime).build());
		sendPacket(player, new PacketBuilder(PacketType.Play.Server.TITLE).writeTitleAction(0, TitleAction.TITLE)
				.writeChatComponents(0, WrappedChatComponent.fromText(title)).build());
		sendPacket(player, new PacketBuilder(PacketType.Play.Server.TITLE).writeTitleAction(0, TitleAction.SUBTITLE)
				.writeChatComponents(0, WrappedChatComponent.fromText(subtitle)).build());
	}

	@Override
	public void reset(Player player) {
		sendPacket(player,
				new PacketBuilder(PacketType.Play.Server.TITLE).writeTitleAction(0, TitleAction.CLEAR).build());
	}

	@Override
	public void broadcast() {

	}

	private void sendPacket(Player player, PacketContainer packet) {
		try {
			BukkitMain.getInstance().getProcotolManager().sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

}
