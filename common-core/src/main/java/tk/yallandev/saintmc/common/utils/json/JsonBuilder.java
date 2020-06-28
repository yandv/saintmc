package tk.yallandev.saintmc.common.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


/**
 * 
 * Helps to create a simple gson with a simple constructor
 * Writted by yandv
 * 
 * @author yandv
 *
 */

public class JsonBuilder {
	
	private JsonObject jsonObject;
	
	public JsonBuilder() {
		jsonObject = new JsonObject();
	}
	
	public JsonBuilder add(String key, JsonElement value) {
		jsonObject.add(key, value);
		return this;
	}
	
	public JsonBuilder addProperty(String key, String value) {
		jsonObject.addProperty(key, value);
		return this;
	}
	
	public JsonBuilder addProperty(String key, Boolean value) {
		jsonObject.addProperty(key, value);
		return this;
	}
	
	public JsonBuilder addProperty(String key, Number value) {
		jsonObject.addProperty(key, value);
		return this;
	}
	
	public JsonBuilder addProperty(String key, Character value) {
		jsonObject.addProperty(key, value);
		return this;
	}
	
	public JsonObject build() {
		return jsonObject;
	}
	
	public static JsonBuilder createObjectBuilder() {
		return new JsonBuilder();
	}

}
