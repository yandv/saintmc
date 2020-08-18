package tk.yallandev.saintmc.bungee.listener;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ClientConnectEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.bungee.bungee.BotMember;
import tk.yallandev.saintmc.bungee.event.BlockAddressEvent;
import tk.yallandev.saintmc.bungee.event.ClearVerifyingEvent;
import tk.yallandev.saintmc.bungee.event.UnblockAddressEvent;
import tk.yallandev.saintmc.common.utils.supertype.FutureCallback;
import tk.yallandev.saintmc.common.utils.web.WebHelper.Method;

/**
 * 
 * Class created to prevent bot connection in the server The focus is block ip
 * and cancel using ClientConnectionEvent (from waterfall)
 * 
 * Check the ip using javascript-coded backend, that made the dirty work and
 * requests the ip on a lot of ip checker
 * 
 * @author yandv
 *
 */

public class LoginListener implements Listener {

	private static final int MAX_VERIFY = 20;

	private LoadingCache<String, JsonObject> cache;

	private Set<String> blockedAddress;
	private int verifyingAddresses;

	public LoginListener() {
		blockedAddress = new HashSet<>();

		cache = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES)
				.build(new CacheLoader<String, JsonObject>() {

					@Override
					public JsonObject load(String key) throws Exception {
						return null;
					}

				});
	}

	/*
	 * Block all ip addresses that are bots
	 */

	@EventHandler
	public void onClientConnect(ClientConnectEvent event) {
		SocketAddress socket = event.getSocketAddress();

		if (socket instanceof InetSocketAddress) {
			InetSocketAddress inetSocketAddress = (InetSocketAddress) socket;

			if (blockedAddress.contains(inetSocketAddress.getHostString()))
				event.setCancelled(true);
			else {
				BotMember botMember = BungeeMain.getInstance().getBotController()
						.getBotMember(inetSocketAddress.getHostString());

				if (botMember.isBlocked())
					event.setCancelled(true);
			}
		}
	}

	/*
	 * Verify if the player is a bot
	 */

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogin(PreLoginEvent event) {
		String playerName = event.getConnection().getName();
		String ipAddress = event.getConnection().getAddress().getHostString();

		/*
		 * Check username playerName startsWith "mcdrop"
		 */

		if (playerName.toLowerCase().startsWith("mcdrop")) {
			event.setCancelled(true);
			event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
					+ "\n§f\n§fPara evitar §cataques de bot§f, bloqueamos todos os nicks que possuam §c\"mcdrop\"§f\n§6Mais informação em: §b"
					+ CommonConst.DISCORD);

			blockAddress(ipAddress);
			return;
		}

		BotMember botMember = BungeeMain.getInstance().getBotController().getBotMember(ipAddress);

		botMember.addName(playerName);

		if (botMember.tooMany()) {
			botMember.block();
			CommonGeneral.getInstance().debug("The address " + ipAddress + " has been blocked for too many nicknames!");
			return;
		}

		if (cache.asMap().containsKey(ipAddress)) {
			JsonObject jsonObject = cache.getIfPresent(ipAddress);

			if (jsonObject.has("allow"))
				if (!jsonObject.get("allow").getAsBoolean()) {
					event.setCancelled(true);
					event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX + "\n§f\n§cSeu endereço de ip foi bloqueado!"
							+ "\n\n§fMotivo: §7" + jsonObject.get("message").getAsString()
							+ "\n§f\n§6Mais informação em: §b" + CommonConst.DISCORD);
					blockAddress(ipAddress);
				}

			return;
		}

		if (!AccountListener.PLAYER_LIST.contains(playerName))
			if (verifyingAddresses >= MAX_VERIFY) {
				event.setCancelled(true);
				event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
						+ "\n§f\n§fEstamos verificando vários ips ao mesmo tempo§f, por favor aguarde para entrar§f\n§6Mais informação em: §b"
						+ CommonConst.DISCORD);
				return;
			}

		event.registerIntent(BungeeMain.getPlugin());
		ProxyServer.getInstance().getScheduler().runAsync(BungeeMain.getPlugin(), new Runnable() {
			@Override
			public void run() {
				/*
				 * Verify if the player is cracked or premium
				 */

				try {
					CommonConst.DEFAULT_WEB.doRequest(
							CommonConst.MOJANG_FETCHER + "session/?ip=" + URLEncoder.encode(ipAddress, "UTF-8"),
							Method.GET, new FutureCallback<JsonElement>() {

								@Override
								public void result(JsonElement result, Throwable error) {
									if (error == null) {
										JsonObject jsonObject = result.getAsJsonObject();

										if (!jsonObject.get("allow").getAsBoolean()) {
											event.setCancelled(true);
											event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
													+ "\n§f\n§cSeu endereço de ip foi bloqueado!" + "\n\n§fMotivo: §7"
													+ jsonObject.get("message").getAsString()
													+ "\n§f\n§6Mais informação em: §b" + CommonConst.DISCORD);
											blockAddress(ipAddress);
										}

										cache.put(ipAddress, jsonObject);
									} else {
										error.printStackTrace();
										event.setCancelled(true);
										event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
												+ "\n§f\n§fNão foi possível verificar o seu ip!§f\n§6Mais informação em: §b"
												+ CommonConst.DISCORD);
									}

									event.completeIntent(BungeeMain.getPlugin());
									verifyingAddresses--;
								}

							});
				} catch (UnsupportedEncodingException e) {
					event.setCancelled(true);
					event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
							+ "\n§f\n§fNão foi possível verificar o seu ip!§f\n§6Mais informação em: §b"
							+ CommonConst.DISCORD);
					event.completeIntent(BungeeMain.getPlugin());
					verifyingAddresses--;
				}
			}
		});
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent event) {
		cache.put(event.getPlayer().getAddress().getHostString(), new JsonObject());
	}

	@EventHandler
	public void onUnblockAddress(UnblockAddressEvent event) {
		blockedAddress.remove(event.getIpAddress());
		cache.invalidate(event.getIpAddress());
	}

	@EventHandler
	public void onBlockAddress(BlockAddressEvent event) {
		if (!blockedAddress.contains(event.getIpAddress()))
			blockedAddress.add(event.getIpAddress());
	}

	@EventHandler
	public void onClearVerifying(ClearVerifyingEvent event) {
		blockedAddress.clear();
		verifyingAddresses = 0;
	}

	public void blockAddress(String ipAddress) {
		blockedAddress.add(ipAddress);
		CommonGeneral.getInstance().debug("The address " + ipAddress + " has been blocked!");

		ProxyServer.getInstance().getScheduler().runAsync(BungeeMain.getInstance(), new Runnable() {

			@Override
			public void run() {
				try {
					CommonConst.DEFAULT_WEB.doRequest(
							CommonConst.API + "/ip/?ip" + URLEncoder.encode(ipAddress, "UTF-8") + "&allowed=false",
							Method.POST);
				} catch (Exception ex) {
				}
			}

		});
	}

}
