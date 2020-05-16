package tk.yallandev.saintmc.common.utils.json;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class JsonUtils {

    protected static final Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC, Modifier.PROTECTED).create();

    public static JsonObject jsonTree(Object src) {
        return gson.toJsonTree(src).getAsJsonObject();
    }

    public static Object elementToBson(JsonElement element) {
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isString()) {
                return primitive.getAsString();
            } else if (primitive.isNumber()) {
                return primitive.getAsNumber();
            } else if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            }
        }
        return Document.parse(gson.toJson(element));
    }

    public static String elementToString(JsonElement element) {
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isString()) {
                return primitive.getAsString();
            }
        }
        return gson.toJson(element);
    }

    public static <T> T mapToObject(Map<String, String> map, Class<T> clazz) {
        JsonObject obj = new JsonObject();
        
        for (Entry<String, String> entry : map.entrySet()) {
            try {
                obj.add(entry.getKey(), JsonParser.parseString(entry.getValue()));
            } catch (Exception e) {
                obj.addProperty(entry.getKey(), entry.getValue());
            }
        }
        
        return gson.fromJson(obj, clazz);
    }

    public static Map<String, String> objectToMap(Object src) {
        Map<String, String> map = new HashMap<>();
        JsonObject obj = (JsonObject) gson.toJsonTree(src);
        
        for (Entry<String, JsonElement> entry : obj.entrySet()) {
            map.put(entry.getKey(), gson.toJson(entry.getValue()));
        }
        
        return map;
    }
}
