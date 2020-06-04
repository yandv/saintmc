package tk.yallandev.saintmc.common.networking;

import java.util.HashSet;
import java.util.Set;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import tk.yallandev.saintmc.common.networking.packet.Packet;

public abstract class PacketHandler {

	private static final Set<PacketHandler> OBSERVER_SET;

	static {
		OBSERVER_SET = new HashSet<>();
	}

	public static void registerHandler(PacketHandler handler) {
		OBSERVER_SET.add(handler);
	}

	public static void firePacket(Packet packet) {
		OBSERVER_SET.forEach(handler -> handler.handlePacket(packet));
	}

	public static Packet decodePacket(byte[] bytes) {

		ByteArrayDataInput in = ByteStreams.newDataInput(bytes);

//		ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
//		DataInputStream msgout = new DataInputStream(in);

//			int groupId = input.readByte();
//			this.rank = Core.getCoreManager().getPermissionManager().getRank(groupId);
//			if (rank == null) {
//				throw new IllegalArgumentException("Invalid group id: " + groupId);
//			}
//			this.permission = input.readUTF();
//			this.value = input.readBoolean();

		return new Packet();
	}

	public abstract void handlePacket(Packet packet);

}
