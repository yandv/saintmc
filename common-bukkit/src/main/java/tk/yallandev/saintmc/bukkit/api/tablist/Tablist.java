package tk.yallandev.saintmc.bukkit.api.tablist;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import tk.yallandev.saintmc.bukkit.BukkitMain;

public abstract class Tablist {

	private String header;
	private String footer;

	private List<UUID> viewerList;

	public Tablist(String header, String footer) {
		this.header = header;
		this.footer = footer;

		this.viewerList = new ArrayList<>();
	}

	public void setFooter(String footer) {
		this.footer = footer;
		updateTab();

	}

	public void setHeader(String header) {
		this.header = header;
		updateTab();
	}

	public void updateTab() {
		for (UUID viewer : viewerList) {
			Player player = Bukkit.getPlayer(viewer);
			PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);

			String[] headerAndFooter = replace(player, header, footer);
			String playerHeader = headerAndFooter[0];
			String playerFooter = headerAndFooter[1];

			packet.getChatComponents().write(0, WrappedChatComponent.fromText(playerHeader));
			packet.getChatComponents().write(1, WrappedChatComponent.fromText(playerFooter));

			try {
				BukkitMain.getInstance().getProcotolManager().sendServerPacket(player, packet);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateTab(Player player) {
		if (!viewerList.contains(player.getUniqueId()))
			return;
		
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);

		String[] headerAndFooter = replace(player, header, footer);
		String playerHeader = headerAndFooter[0];
		String playerFooter = headerAndFooter[1];

		packet.getChatComponents().write(0, WrappedChatComponent.fromText(playerHeader));
		packet.getChatComponents().write(1, WrappedChatComponent.fromText(playerFooter));

		try {
			BukkitMain.getInstance().getProcotolManager().sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void addViewer(Player player) {
		if (viewerList.contains(player.getUniqueId()))
			return;

		String[] headerAndFooter = replace(player, header, footer);
		String playerHeader = headerAndFooter[0];
		String playerFooter = headerAndFooter[1];

		PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
		packet.getChatComponents().write(0, WrappedChatComponent.fromText(playerHeader));
		packet.getChatComponents().write(1, WrappedChatComponent.fromText(playerFooter));

		try {
			BukkitMain.getInstance().getProcotolManager().sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		viewerList.add(player.getUniqueId());
	}

	public void removeViewer(Player player) {
		if (!viewerList.contains(player.getUniqueId()))
			return;

		PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
		packet.getChatComponents().write(0, WrappedChatComponent.fromText(" "));
		packet.getChatComponents().write(1, WrappedChatComponent.fromText(" "));

		try {
			BukkitMain.getInstance().getProcotolManager().sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		viewerList.remove(player.getUniqueId());
	}

	public void broadcastHeaderAndFooter() {
		for (Player player : Bukkit.getOnlinePlayers())
			addViewer(player);
	}

	public abstract String[] replace(Player player, String header, String footer);

}
