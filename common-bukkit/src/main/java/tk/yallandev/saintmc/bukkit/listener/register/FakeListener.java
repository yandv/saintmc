package tk.yallandev.saintmc.bukkit.listener.register;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.player.FakePlayerAPI;
import tk.yallandev.saintmc.bukkit.listener.Listener;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.utils.supertype.FutureCallback;

public class FakeListener extends Listener {

	private LoadingCache<UUID, WrappedSignedProperty> skinCache;

	public FakeListener() {
		skinCache = CacheBuilder.newBuilder().expireAfterWrite(3L, TimeUnit.MINUTES)
				.build(new CacheLoader<UUID, WrappedSignedProperty>() {

					@Override
					public WrappedSignedProperty load(UUID key) throws Exception {
						return null;
					}

				});
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
			if (skinCache.asMap().containsKey(event.getUniqueId()))
				return;

			getMain().getSkinManager().getSkin(event.getName(), new FutureCallback<JsonElement>() {

				@Override
				public void result(JsonElement result, Throwable error) {
					if (error != null) {
						return;
					}

					JsonObject jsonObject = result.getAsJsonObject();

					WrappedSignedProperty proper = null;

					if (jsonObject != null) {
						if (jsonObject.has("properties")) {
							JsonArray jsonArray = jsonObject.get("properties").getAsJsonArray();

							for (int x = 0; x < jsonArray.size(); x++) {
								JsonObject json = (JsonObject) jsonArray.get(x);

								if (json.get("name").getAsString().equalsIgnoreCase("textures")) {
									proper = new WrappedSignedProperty(json.get("name").getAsString(),
											json.get("value").getAsString(), json.get("signature").getAsString());
								}
							}
						}
					}

					if (proper == null)
						return;

					skinCache.put(event.getUniqueId(), proper);
				}
			});
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		if (member.isUsingFake())
			if (member.isLastServer(ServerType.PRIVATE_SERVER)) {
				member.setFakeName(member.getPlayerName());
			} else {
				FakePlayerAPI.changePlayerName(player, member.getFakeName());
				member.sendMessage("§aVocê está usando o fake " + member.getFakeName() + "!");
			}

		if (skinCache.asMap().containsKey(player.getUniqueId())) {
			if (member.getSessionTime() <= 5000)
				member.sendMessage("§aSua skin foi alterada!");

			FakePlayerAPI.changePlayerSkin(player, skinCache.getIfPresent(player.getUniqueId()), true);
		}
	}

}
