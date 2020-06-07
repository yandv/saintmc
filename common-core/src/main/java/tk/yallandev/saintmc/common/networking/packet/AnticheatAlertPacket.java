package tk.yallandev.saintmc.common.networking.packet;

import com.google.gson.JsonObject;

import tk.yallandev.saintmc.common.networking.Packet;
import tk.yallandev.saintmc.common.networking.PacketType;
import tk.yallandev.saintmc.common.utils.json.JsonBuilder;

public class AnticheatAlertPacket extends Packet {

	public AnticheatAlertPacket(JsonObject jsonObject) {
		super(jsonObject);
	}

	public AnticheatAlertPacket(String hackType, int cps, int alerts, int maxAlerts) {
		super(new JsonBuilder().addProperty("packetType", PacketType.ANTICHEAT_ALERT.name())
				.addProperty("hackType", hackType).addProperty("cps", cps).addProperty("alerts", alerts).addProperty("maxAlerts", maxAlerts).build());
	}

	public String getHackType() {
		return getJsonObject().get("hackType").getAsString();
	}
	
	public int getCps() {
		return getJsonObject().get("cps").getAsInt();
	}
	
	public int getAlerts() {
		return getJsonObject().get("alerts").getAsInt();
	}

	public int getMaxAlerts() {
		return getJsonObject().get("maxAlerts").getAsInt();
	}

}
