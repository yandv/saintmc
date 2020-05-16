package tk.yallandev.saintmc.bukkit.controller;

import java.io.IOException;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.common.account.Member;

public class SkinController {

	public JsonElement saveSkin(Member member, WrappedSignedProperty property) {
		try {
			HttpPost post = new HttpPost(CommonConst.SKIN_URL + "?name=" + member.getPlayerName());

			JsonObject jsonObject = new JsonObject();

			jsonObject.addProperty("name", member.getPlayerName());

			JsonArray jsonArray = new JsonArray();
			JsonObject properties = new JsonObject();

			properties.addProperty("name", property.getName());
			properties.addProperty("value", property.getValue());
			properties.addProperty("signature", property.getSignature());
			jsonArray.add(properties);

			jsonObject.add("properties", jsonArray);

			StringEntity postingString = new StringEntity(jsonObject.toString());
			post.setEntity(postingString);
			post.setHeader("Content-type", "application/json");

			CloseableHttpResponse response = CommonConst.HTTPCLIENT.execute(post);
			String json = EntityUtils.toString(response.getEntity());
			response.close();
			return JsonParser.parseString(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return JsonNull.INSTANCE;
	}

	public JsonElement deleteSkin(Member member) {
		try {
			HttpDelete delete = new HttpDelete(CommonConst.SKIN_URL + "" + member.getPlayerName());

			delete.setHeader("Content-type", "application/json");

			CloseableHttpResponse response = CommonConst.HTTPCLIENT.execute(delete);
			String json = EntityUtils.toString(response.getEntity());
			response.close();
			return JsonParser.parseString(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return JsonNull.INSTANCE;
	}

	public JsonObject getSkin(String playerName) {
		HttpGet request = new HttpGet(CommonConst.SKIN_URL + "?name=" + playerName);

		request.addHeader("custom-key", "mkyong");
		request.addHeader(HttpHeaders.USER_AGENT, "Googlebot");

		try (CloseableHttpResponse response = CommonConst.HTTPCLIENT.execute(request)) {
			String jsonString = EntityUtils.toString(response.getEntity());
			response.close();

			if (jsonString != null) {
				JsonObject json = (JsonObject) JsonParser.parseString(jsonString);

				if (json != null) {
					if (json.has("properties")) {
						JsonArray jsonArray = json.get("properties").getAsJsonArray();

						for (int x = 0; x < jsonArray.size(); x++) {
							JsonObject jsonObject = (JsonObject) jsonArray.get(x);

							if (jsonObject.get("name").getAsString().equalsIgnoreCase("textures")) {
//								WrappedSignedProperty property = new WrappedSignedProperty(
//										jsonObject.get("name").getAsString(), jsonObject.get("value").getAsString(),
//										jsonObject.get("signature").getAsString());
								return jsonObject;
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
}
