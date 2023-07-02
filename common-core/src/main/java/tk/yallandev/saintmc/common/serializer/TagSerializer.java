package tk.yallandev.saintmc.common.serializer;

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
import tk.yallandev.saintmc.common.utils.json.JsonBuilder;

public class TagSerializer implements JsonDeserializer<Tag>, JsonSerializer<Tag> {

	@Override
	public JsonElement serialize(Tag src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonBuilder().addProperty("name", src.getName()).addProperty("chroma", src.isChroma()).build();
	}

	@Override
	public Tag deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {// abre o server ai pra euver se vai ocorrer tudo certinho

		Tag tag = null;
		boolean chroma = false;

		if (json instanceof JsonObject) {
			JsonObject jsonObject = json.getAsJsonObject();

			tag = Tag.valueOf(jsonObject.get("name").getAsString());
			if (jsonObject.has("chroma"))
				chroma = jsonObject.get("chroma").getAsBoolean();
		} else if (json instanceof JsonPrimitive) {
			tag = Tag.valueOf(json.getAsJsonPrimitive().getAsString());
		}

		if (tag != null)
			if (chroma)
				tag.setChroma(chroma);

		return tag == null ? Tag.MEMBRO : tag;
	}

}
