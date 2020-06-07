package tk.yallandev.saintmc.common.networking;

import com.google.gson.JsonObject;

import lombok.Getter;

/**
 * 
 * Packet Class
 * 
 * How to create a packet ?
 * 
 * When WRITING, you need write all fields in jsonObject
 * When READING, you need read the necessaries fields from jsonObject
 * 
 * @author yandv
 *
 */

@Getter
public abstract class Packet {
	
	private JsonObject jsonObject;

	public Packet(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}
	
}
