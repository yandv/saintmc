package tk.yallandev.saintmc.common.utils.mojang;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.utils.supertype.FutureCallback;
import tk.yallandev.saintmc.common.utils.web.WebHelper.Method;

public class MojangFetcher {

	private LoadingCache<String, Boolean> crackCache;
	private LoadingCache<String, UUID> uuidCache;

	public MojangFetcher() {
		crackCache = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES)
				.build(new CacheLoader<String, Boolean>() {
					@Override
					public Boolean load(String playerName) throws Exception {
						return requestCracked(playerName);
					}
				});

		uuidCache = CacheBuilder.newBuilder().expireAfterWrite(3L, TimeUnit.HOURS)
				.build(new CacheLoader<String, UUID>() {
					@Override
					public UUID load(String playerName) throws Exception {
						UUID uuid = CommonGeneral.getInstance().getCommonPlatform().getUuid(playerName);
						return uuid == null ? requestUuid(playerName) : uuid;
					}

				});
	}

	public boolean isCracked(String playerName) {
		try {
			return crackCache.get(playerName);
		} catch (Exception ex) {
		}

		return true;
	}

	public UUID getUuid(String playerName) {
		if (playerName.matches("[a-zA-Z0-9_]{3,16}")) {
			try {
				return uuidCache.get(playerName);
			} catch (Exception ex) {
				return null;
			}
		} else {
			return UUIDParser.parse(playerName);
		}
	}

	public UUID requestUuid(String playerName) {
		try {
			JsonObject jsonObject = (JsonObject) CommonConst.DEFAULT_WEB
					.doRequest(CommonConst.MOJANG_FETCHER + playerName, Method.GET);

			if (jsonObject.has("id"))
				return UUIDParser.parse(jsonObject.get("id").getAsString());
		} catch (IllegalArgumentException ex) {
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public boolean requestCracked(String playerName) {
		return requestUuid(playerName) != null;
	}

	public void isCracked(String playerName, FutureCallback<Boolean> futureCallback) {
		if (crackCache.asMap().containsKey(playerName)) {
			futureCallback.result(crackCache.getIfPresent(playerName), null);
			return;
		}

		try {
			CommonConst.DEFAULT_WEB.doRequest(CommonConst.MOJANG_FETCHER + "?name=" + playerName, Method.GET,
					new FutureCallback<JsonElement>() {

						@Override
						public void result(JsonElement result, Throwable error) {
							if (error == null) {
								boolean cracked = result.getAsJsonObject().get("cracked").getAsBoolean();
								futureCallback.result(cracked, error);
								crackCache.put(playerName, cracked);
							} else {
								futureCallback.result(false, error);
							}
						}

					});
		} catch (Exception e) {
			futureCallback.result(false, e);
		}
	}

}
