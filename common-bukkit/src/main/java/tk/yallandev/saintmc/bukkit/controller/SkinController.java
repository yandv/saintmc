package tk.yallandev.saintmc.bukkit.controller;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.utils.supertype.FutureCallback;
import tk.yallandev.saintmc.common.utils.web.WebHelper.Method;

public class SkinController {

	public void saveSkin(Member member, WrappedSignedProperty property) {
		JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("name", member.getPlayerName());

		JsonArray jsonArray = new JsonArray();
		JsonObject properties = new JsonObject();

		properties.addProperty("name", property.getName());
		properties.addProperty("value", property.getValue());
		properties.addProperty("signature", property.getSignature());
		jsonArray.add(properties);

		jsonObject.add("properties", jsonArray);

		CommonConst.DEFAULT_WEB.doAsyncRequest(CommonConst.SKIN_URL + "?name=" + member.getPlayerName(), Method.POST,
				jsonObject.toString(), new FutureCallback<JsonElement>() {

					@Override
					public void result(JsonElement result, Throwable error) {
						if (error == null) {
							CommonGeneral.getInstance()
									.debug("The skin of " + member.getPlayerName() + " has been saved!");
						}
					}
				});

	}

	public void deleteSkin(Member member) {
		CommonConst.DEFAULT_WEB.doAsyncRequest(CommonConst.SKIN_URL + member.getPlayerName(), Method.DELETE,
				null, new FutureCallback<JsonElement>() {

					@Override
					public void result(JsonElement result, Throwable error) {
						if (error == null) {
							CommonGeneral.getInstance()
									.debug("The skin of " + member.getPlayerName() + " has been deleted!");
						}
					}
				});
	}

	public WrappedSignedProperty getSkin(String playerName) {

		try {
			JsonObject json = (JsonObject) CommonConst.DEFAULT_WEB
					.doRequest(CommonConst.SKIN_URL + "?name=" + playerName, Method.GET);

			if (json != null) {
				if (json.has("properties")) {
					JsonArray jsonArray = json.get("properties").getAsJsonArray();

					for (int x = 0; x < jsonArray.size(); x++) {
						JsonObject jsonObject = (JsonObject) jsonArray.get(x);

						if (jsonObject.get("name").getAsString().equalsIgnoreCase("textures")) {
							return new WrappedSignedProperty(jsonObject.get("name").getAsString(),
									jsonObject.get("value").getAsString(), jsonObject.get("signature").getAsString());
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void getSkin(String playerName, FutureCallback<JsonElement> futureCallback) {
		CommonConst.DEFAULT_WEB.doAsyncRequest(CommonConst.SKIN_URL + "?name=" + playerName, Method.GET,
				futureCallback);
	}
}
