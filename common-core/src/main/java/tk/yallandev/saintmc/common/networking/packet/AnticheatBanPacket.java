package tk.yallandev.saintmc.common.networking.packet;

import com.google.gson.JsonObject;

import tk.yallandev.saintmc.common.networking.Packet;
import tk.yallandev.saintmc.common.networking.PacketType;
import tk.yallandev.saintmc.common.utils.json.JsonBuilder;

public class AnticheatBanPacket extends Packet {

	public AnticheatBanPacket(JsonObject jsonObject) {
		super(jsonObject);
	}

	public AnticheatBanPacket(String hackType, long banTime) {
		super(new JsonBuilder().addProperty("packetType", PacketType.ANTICHEAT_BAN.name())
		.addProperty("hackType", hackType)
		.addProperty("banTime", banTime).build());
	}
	
	public String getHackType() {
		return getJsonObject().get("hackType").getAsString();
	}
	
	public long getBanTime() {
		return getJsonObject().get("banTime").getAsLong();
	}

}
