package tk.yallandev.saintmc.bungee.listener;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ClientConnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.StrangePacketEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.bungee.bungee.BotMember;
import tk.yallandev.saintmc.bungee.event.IpRemoveEvent;
import tk.yallandev.saintmc.common.utils.ip.IpFetcher;
import tk.yallandev.saintmc.common.utils.ip.IpInfo;
import tk.yallandev.saintmc.common.utils.ip.IpInfo.IpStatus;

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

	public static final int MAX_VERIFY = 20;

	private LoadingCache<String, IpInfo> cache;

	public LoginListener() {
		cache = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES)
				.build(new CacheLoader<String, IpInfo>() {

					@Override
					public IpInfo load(String key) throws Exception {
						return IpFetcher.IP_FETCHER.fetchAddress(key);
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

			if (BungeeMain.getInstance().getBotController().getBlockedAddress()
					.contains(inetSocketAddress.getHostString())) {
				event.setCancelled(true);
				return;
			}

			if (BungeeMain.getInstance().getBotController().containsKey(inetSocketAddress.getHostString())) {
				BotMember botMember = BungeeMain.getInstance().getBotController()
						.getBotMember(inetSocketAddress.getHostString());

				if (botMember.isBlocked())
					event.setCancelled(true);
			}
		}
	}

	/*
	 * Block the ip that call StrangePacket
	 */

	@EventHandler(priority = EventPriority.NORMAL)
	public void onLogin(StrangePacketEvent event) {
		SocketAddress socket = event.getSocketAddress();

		if (socket instanceof InetSocketAddress) {
			String ipAddress = ((InetSocketAddress) socket).getHostString();

			blockIp(ipAddress, false);
			CommonGeneral.getInstance().debug("The address " + ipAddress + " has been blocked by strange packet!");
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

			blockIp(ipAddress, false);
			return;
		}

		BotMember botMember = BungeeMain.getInstance().getBotController().getBotMember(ipAddress);

		if (botMember.isBlocked()) {
			event.setCancelled(true);
			event.setCancelReason(
					"§c§lANTIBOT\n\n§cVocê foi bloqueado de entrar!\n§bMais informações em: " + CommonConst.DISCORD);
			return;
		}

		botMember.addName(playerName);

		if (botMember.tooMany()) {
			event.setCancelled(true);
			event.setCancelReason(
					"§c§lANTIBOT\n\n§cVocê foi bloqueado de entrar!\n§bMais informações em: " + CommonConst.DISCORD);

			botMember.block();
			CommonGeneral.getInstance().debug("The address " + ipAddress + " has been blocked for too many nicknames!");
			blockIp(ipAddress, false);
			return;
		}

		if (cache.asMap().containsKey(ipAddress)) {
			IpInfo ipInfo = cache.getIfPresent(ipAddress);

			switch (ipInfo.getIpStatus()) {
			case NOT_FROM_BRAZIL: {
				event.setCancelled(true);
				event.setCancelReason(
						"§c§lANTIBOT\n\n§cO seu endereço de ip foi bloqueado por não ser do Brasil!\n§bMais informações em: "
								+ CommonConst.DISCORD);
				blockIp(ipAddress, false);
				CommonGeneral.getInstance()
						.debug("The address " + ipAddress + " has been blocked for isnt a brazilian address!");
				break;
			}
			case BLOCKED: {
				event.setCancelled(true);
				event.setCancelReason("§c§lANTIBOT\n\n§cO seu endereço de ip foi bloqueado!\n§bMais informações em: "
						+ CommonConst.DISCORD);
				blockIp(ipAddress, false);
				CommonGeneral.getInstance().debug("The address " + ipAddress + " has been blocked!");
				break;
			}
			case ACCEPT: {
				break;
			}
			}

			return;
		}

//		event.registerIntent(BungeeMain.getPlugin());
//		ProxyServer.getInstance().getScheduler().runAsync(BungeeMain.getPlugin(), new Runnable() {
//			@Override
//			public void run() {
//				/*
//				 * Verify if the player ip is valid
//				 */
//
//				IpInfo ipInfo = cache.asMap().containsKey(ipAddress) ? cache.asMap().get(ipAddress)
//						: CommonGeneral.getInstance().getIpData().loadIp(ipAddress);
//
//				if (ipInfo == null) {
//					try {
//						ipInfo = cache.getIfPresent(ipAddress);
//						ipInfo.check();
//
//						CommonGeneral.getInstance().getIpData().registerIp(ipInfo);
//					} catch (Exception ex) {
//						event.completeIntent(BungeeMain.getInstance());
//						CommonGeneral.getInstance().debug("The address " + ipAddress + " hasn't been loaded!");
//						return;
//					}
//				} else
//					cache.put(ipAddress, ipInfo);
//
//				switch (ipInfo.getIpStatus()) {
//				case NOT_FROM_BRAZIL: {
//					event.setCancelled(true);
//					event.setCancelReason(
//							"§c§lANTIBOT\n\n§cO seu endereço de ip foi bloqueado por não ser do Brasil!\n§bMais informações em: "
//									+ CommonConst.DISCORD);
//					blockIp(ipAddress, true);
//					CommonGeneral.getInstance()
//							.debug("The address " + ipAddress + " has been blocked for isnt a brazilian address!");
//					break;
//				}
//				case BLOCKED: {
//					event.setCancelled(true);
//					event.setCancelReason(
//							"§c§lANTIBOT\n\n§cO seu endereço de ip foi bloqueado!\n§bMais informações em: "
//									+ CommonConst.DISCORD);
//					blockIp(ipAddress, true);
//					CommonGeneral.getInstance().debug("The address " + ipAddress + " has been blocked!");
//					break;
//				}
//				case ACCEPT: {
//					break;
//				}
//				}
//
//				event.completeIntent(BungeeMain.getPlugin());
//			}
//		});
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onIpRemove(IpRemoveEvent event) {
		String ipAddress = event.getIpAddress();

		CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

			@Override
			public void run() {
				IpInfo ipInfo = CommonGeneral.getInstance().getIpData().loadIp(ipAddress);

				if (ipInfo == null) {
					try {
						ipInfo = cache.get(ipAddress);
						ipInfo.check();

						CommonGeneral.getInstance().getIpData().registerIp(ipInfo);
					} catch (ExecutionException e) {
						return;
					}
				} else
					cache.put(ipAddress, ipInfo);

				ipInfo.setIpStatus(IpStatus.ACCEPT);
			}
		});
	}

	public void blockIp(String ipAddress, boolean async) {
		BungeeMain.getInstance().getBotController().blockIp(ipAddress);

		if (async) {
			IpInfo ipInfo = CommonGeneral.getInstance().getIpData().loadIp(ipAddress);

			if (ipInfo == null) {
				try {
					ipInfo = cache.get(ipAddress);
					ipInfo.check();

					CommonGeneral.getInstance().getIpData().registerIp(ipInfo);
				} catch (ExecutionException e) {
					return;
				}
			} else
				cache.put(ipAddress, ipInfo);

			if (ipInfo.getIpStatus() == IpStatus.BLOCKED || ipInfo.getIpStatus() == IpStatus.NOT_FROM_BRAZIL)
				ipInfo.setIpStatus(IpStatus.BLOCKED);
		} else
			CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

				@Override
				public void run() {
					IpInfo ipInfo = CommonGeneral.getInstance().getIpData().loadIp(ipAddress);

					if (ipInfo == null) {
						try {
							ipInfo = cache.get(ipAddress);
							ipInfo.check();

							CommonGeneral.getInstance().getIpData().registerIp(ipInfo);
						} catch (ExecutionException e) {
							return;
						}
					} else
						cache.put(ipAddress, ipInfo);

					if (ipInfo.getIpStatus() == IpStatus.BLOCKED || ipInfo.getIpStatus() == IpStatus.NOT_FROM_BRAZIL)
						ipInfo.setIpStatus(IpStatus.BLOCKED);
				}
			});
	}

}
