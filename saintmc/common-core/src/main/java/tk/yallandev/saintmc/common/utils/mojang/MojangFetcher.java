package tk.yallandev.saintmc.common.utils.mojang;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;

public class MojangFetcher {

//	private static final ExecutorService THREAD = Executors.newCachedThreadPool();

	private LoadingCache<String, Boolean> cache;
	private LoadingCache<String, UUID> cacheUuid;

	public MojangFetcher() {
		cache = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.MINUTES)
				.build(new CacheLoader<String, Boolean>() {
					@Override
					public Boolean load(String playerName) throws Exception {
						return requestCracked(playerName);
					}
				});

		cacheUuid = CacheBuilder.newBuilder().expireAfterWrite(2L, TimeUnit.HOURS)
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
			return cache.get(playerName);
		} catch (Exception ex) {
		}

		return true;
	}

	public UUID getUuid(String playerName) {
		if (playerName.matches("[a-zA-Z0-9_]{3,16}")) {
			try {
				return cacheUuid.get(playerName);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			return UUIDParser.parse(playerName);
		}

		return null;
	}

	public static void main(String[] args) {
		MojangFetcher fetcher = new MojangFetcher();

		System.out.println(fetcher.getUuid("yAllanDev_"));
		System.out.println(fetcher.getUuid("yandv"));
		System.out.println(fetcher.getUuid("leoctotti"));

		UUID uuid = UUID.randomUUID();

		fetcher.registerUuid("DSOLAKOASKD", uuid);

		System.out.println("eae");
	}

	public void registerUuid(String playerName, UUID uniqueId) {
		try {
			HttpPost httpPost = new HttpPost(CommonConst.MOJANG_FETCHER);

			JsonObject jsonObject = new JsonObject();

			jsonObject.addProperty("name", playerName);
			jsonObject.addProperty("uniqueId", uniqueId.toString().replace("-", ""));
			jsonObject.addProperty("cracked", true);
			jsonObject.addProperty("time", System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 30));

			httpPost.setEntity(new StringEntity(jsonObject.toString()));
			httpPost.setHeader("Content-type", "application/json");
			
			CommonConst.HTTPCLIENT.execute(httpPost);

			CommonGeneral.getInstance()
					.debug("The " + playerName + " (" + uniqueId.toString() + ") has been registred in MojangFetcher!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public UUID requestUuid(String playerName) {
		try {

			HttpGet httpGet = new HttpGet(CommonConst.MOJANG_FETCHER + "?name=" + playerName);

			httpGet.addHeader("Content-type", "application/json");

			CloseableHttpResponse response = CommonConst.HTTPCLIENT.execute(httpGet);
			String json = EntityUtils.toString(response.getEntity());

			JsonObject jsonObject = (JsonObject) JsonParser.parseString(json);

			return UUIDParser.parse(jsonObject.get("uuid").getAsString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public boolean requestCracked(String playerName) {
		try {
			HttpGet httpGet = new HttpGet(CommonConst.MOJANG_FETCHER + "?name=" + playerName);

			httpGet.addHeader("Content-type", "application/json");

			CloseableHttpResponse response = CommonConst.HTTPCLIENT.execute(httpGet);
			String json = EntityUtils.toString(response.getEntity());

			JsonObject jsonObject = (JsonObject) JsonParser.parseString(json);

			return jsonObject.get("cracked").getAsBoolean();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return true;
	}

}
