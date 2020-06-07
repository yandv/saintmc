package tk.yallandev.saintmc.bukkit.networking.packet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.common.controller.PacketController;
import tk.yallandev.saintmc.common.networking.Packet;

public class BukkitPacketController extends PacketController {

	public void sendPacket(Packet packet, Player player) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		try {
			out.writeUTF(packet.getJsonObject().toString());
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

		player.sendPluginMessage(BukkitMain.getInstance(), "server:packet", b.toByteArray());
		
	}

}
