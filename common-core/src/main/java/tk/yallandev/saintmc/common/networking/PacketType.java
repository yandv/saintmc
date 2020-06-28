package tk.yallandev.saintmc.common.networking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.common.networking.packet.AnticheatAlertPacket;
import tk.yallandev.saintmc.common.networking.packet.AnticheatBanPacket;

@AllArgsConstructor
@Getter
public enum PacketType {
	
	ANTICHEAT_BAN(0, AnticheatBanPacket.class),
	ANTICHEAT_ALERT(1, AnticheatAlertPacket.class);
	
	private int packetId;
	private Class<? extends Packet> packetClass;
	
}
