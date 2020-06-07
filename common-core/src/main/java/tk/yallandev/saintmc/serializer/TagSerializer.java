package tk.yallandev.saintmc.serializer;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import tk.yallandev.saintmc.common.tag.Tag;

public class TagSerializer implements JsonDeserializer<Tag>, JsonSerializer<Tag> {

	@Override
	public JsonElement serialize(Tag src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("tag", src.getName());
		return object;
	}

	@Override
	public Tag deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		
		System.out.println(json);
		
		if (json instanceof JsonObject) {
			return Tag.valueOf(json.getAsJsonObject().get("tag").getAsString());
		} else if (json instanceof JsonPrimitive) {
			return Tag.valueOf(json.getAsJsonPrimitive().getAsString());
		}
		
		return Tag.MEMBRO;
	}


}
