package tk.yallandev.saintmc.common.networking.packet;

import com.google.gson.JsonObject;

import tk.yallandev.saintmc.common.networking.Packet;
import tk.yallandev.saintmc.common.networking.PacketType;
import tk.yallandev.saintmc.common.utils.json.JsonBuilder;

public class AnticheatAlertPacket extends Packet {

	public AnticheatAlertPacket(JsonObject jsonObject) {
		super(jsonObject);
	}

	public AnticheatAlertPacket(String hackType, int alertIndex, int maxAlerts, JsonObject jsonObject) {
		super(new JsonBuilder().addProperty("packetType", PacketType.ANTICHEAT_ALERT.name())
				.addProperty("hackType", hackType).addProperty("alertIndex", alertIndex)
				.addProperty("maxAlerts", maxAlerts).add("metadata", jsonObject).build());
	}

	public String getHackType() {
		return getJsonObject().get("hackType").getAsString();
	}
	
	public String getAlertIndex() {
		return getJsonObject().get("alertIndex").getAsString();
	}
	
	public String getMaxAlerts() {
		return getJsonObject().get("maxAlerts").getAsString();
	}

	public JsonObject getMetadata() {
		return getJsonObject().get("metadata").getAsJsonObject();
	}
	
}
