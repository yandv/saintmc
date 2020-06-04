package tk.yallandev.saintmc.common.networking.packet;

import com.google.gson.JsonObject;

import lombok.Getter;
import tk.yallandev.saintmc.common.networking.PacketSender;

@Getter
public class Packet {
	
	private PacketSender packetSender;
	private JsonObject jsonObject;
	
}
