package tk.yallandev.saintmc.bukkit.controller;

import java.util.UUID;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.common.utils.supertype.FutureCallback;
import tk.yallandev.saintmc.common.utils.web.WebHelper.Method;

public class SkinController {

	public WrappedSignedProperty getSkin(UUID uniqueId) {

		try {
			JsonObject json = (JsonObject) CommonConst.DEFAULT_WEB
					.doRequest(String.format(CommonConst.SKIN_URL, uniqueId.toString()), Method.GET);

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

	public void getSkin(UUID uniqueId, FutureCallback<JsonElement> futureCallback) {
		CommonConst.DEFAULT_WEB.doAsyncRequest(String.format(CommonConst.SKIN_URL, uniqueId.toString()), Method.GET,
				futureCallback);
	}
}
