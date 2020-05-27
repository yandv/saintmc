package tk.yallandev.saintmc.bungee.listener;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ClientConnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.common.utils.web.WebHelper.FutureCallback;
import tk.yallandev.saintmc.common.utils.web.WebHelper.Method;

/**
 * 
 * Class created to prevent bot connection in the server
 * The focus is block ip and cancel using ClientConnectionEvent (from waterfall)
 * 
 * Check the ip using javascript-coded backend, that made the dirty work and
 * requests the ip on a lot of ip checker
 * 
 * @author yandv
 *
 */

public class LoginListener implements Listener {
	
	private Set<String> blockedAddress;
	
	public LoginListener() {
		blockedAddress = new HashSet<>();
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

		event.registerIntent(BungeeMain.getPlugin());
		ProxyServer.getInstance().getScheduler().runAsync(BungeeMain.getPlugin(), new Runnable() {
			@Override
			public void run() {
				
				/*
				 * Old Code to verify the ip using BungeeCord HttpClient
				 */
				
//				/*
//				 * Get InitialHandler instance from Connection
//				 */
//
//				InitialHandler initialHandler = (InitialHandler) event.getConnection();
//				
//				/*
//				 * Get ChannelWrapper to use eventLoop from BungeeCord
//				 * to use the own BungeeCord HttpClient
//				 */
//				
//				ChannelWrapper channelWrapper = null;
//
//				try {
//					Field field = initialHandler.getClass().getDeclaredField("ch");
//					field.setAccessible(true);
//					channelWrapper = (ChannelWrapper) field.get(initialHandler);
//				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
//						| IllegalAccessException e) {
//					e.printStackTrace();
//					event.completeIntent(BungeeMain.getPlugin());
//					return;
//				}
//				
//				/*
//				 * Get ChannelWrapper to use eventLoop from BungeeCord
//				 * to use the own BungeeCord HttpClient
//				 */
//
//				try {
//					HttpClient.get(CommonConst.MOJANG_FETCHER + "session/?ip=" + URLEncoder.encode(ipAddress, "UTF-8"),
//							channelWrapper.getHandle().eventLoop(), new Callback<String>() {
//								@Override
//								public void done(String result, Throwable error) {
//									if (error == null) {
//										JsonObject jsonObject = (JsonObject) JsonParser.parseString(result);
//
//										if (!jsonObject.get("allow").getAsBoolean()) {
//											event.setCancelled(true);
//											event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
//													+ "\n§f\n§cSeu endereço de ip foi bloqueado!" + "\n\n§fMotivo: §7"
//													+ jsonObject.get("message").getAsString()
//													+ "\n§f\n§6Mais informação em: §b" + CommonConst.DISCORD);
//											blockAddress(ipAddress);
//										}
//									} else {
//										event.setCancelled(true);
//										event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
//												+ "\n§f\n§fNão foi possível verificar o seu ip!§f\n§6Mais informação em: §b"
//												+ CommonConst.DISCORD);
//									}
//									
//									event.completeIntent(BungeeMain.getPlugin());
//								}
//							});
//				} catch (UnsupportedEncodingException e) {
//					event.setCancelled(true);
//					event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
//							+ "\n§f\n§fNão foi possível verificar o seu ip!§f\n§6Mais informação em: §b"
//							+ CommonConst.DISCORD);
//					event.completeIntent(BungeeMain.getPlugin());
//
//					e.printStackTrace();
//				}
				
				try {
					CommonConst.DEFAULT_WEB.doAsyncRequest(CommonConst.MOJANG_FETCHER + "session/?ip=" + URLEncoder.encode(ipAddress, "UTF-8"), Method.GET, new FutureCallback<JsonElement>() {
						
						@Override
						public void result(JsonElement result, Throwable error) {
							if (error == null) {
								JsonObject jsonObject = (JsonObject) result;

								if (!jsonObject.get("allow").getAsBoolean()) {
									event.setCancelled(true);
									event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
											+ "\n§f\n§cSeu endereço de ip foi bloqueado!" + "\n\n§fMotivo: §7"
											+ jsonObject.get("message").getAsString()
											+ "\n§f\n§6Mais informação em: §b" + CommonConst.DISCORD);
									blockAddress(ipAddress);
								}
							} else {
								event.setCancelled(true);
								event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
										+ "\n§f\n§fNão foi possível verificar o seu ip!§f\n§6Mais informação em: §b"
										+ CommonConst.DISCORD);
							}
							
							event.completeIntent(BungeeMain.getPlugin());
						}
					});
				} catch (UnsupportedEncodingException e) {
					event.setCancelled(true);
					event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
							+ "\n§f\n§fNão foi possível verificar o seu ip!§f\n§6Mais informação em: §b"
							+ CommonConst.DISCORD);
					event.completeIntent(BungeeMain.getPlugin());

					e.printStackTrace();
				}
			}
		});
	}
	
	public void blockAddress(String ipAddress) {
		blockedAddress.add(ipAddress);
		CommonGeneral.getInstance().debug("The address " + ipAddress + " has been blocked!");
	}

}
