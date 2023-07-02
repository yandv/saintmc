package tk.yallandev.saintmc.bungee.listener;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ClientConnectEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.bungee.bungee.BotMember;
import tk.yallandev.saintmc.bungee.bungee.BungeeClan;
import tk.yallandev.saintmc.bungee.bungee.BungeeMember;
import tk.yallandev.saintmc.bungee.event.IpRemoveEvent;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.configuration.LoginConfiguration.AccountType;
import tk.yallandev.saintmc.common.ban.Category;
import tk.yallandev.saintmc.common.ban.constructor.Ban;
import tk.yallandev.saintmc.common.clan.Clan;
import tk.yallandev.saintmc.common.clan.ClanModel;
import tk.yallandev.saintmc.common.clan.event.member.MemberOnlineEvent;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.report.Report;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.utils.ip.FetchAddressException;
import tk.yallandev.saintmc.common.utils.ip.IpFetcher;
import tk.yallandev.saintmc.common.utils.ip.IpInfo;
import tk.yallandev.saintmc.common.utils.ip.IpInfo.IpStatus;
import tk.yallandev.saintmc.common.utils.supertype.FutureCallback;
import tk.yallandev.saintmc.common.utils.web.WebHelper.Method;

public class LoginListener implements Listener {

	public static final int MAX_VERIFY = 20;

	private Map<String, IpInfo> ipMap = new HashMap<>();
	private Cache<String, JsonObject> ipList = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.SECONDS).build();
	private Cache<String, JsonObject> textureCache = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES)
			.build();

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

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPreLogin(PreLoginEvent event) {
		event.registerIntent(BungeeMain.getInstance());
		CommonGeneral.getInstance().getCommonPlatform().runAsync(() -> {

			try {
				event.getConnection().setOnlineMode(checkLogin(event));

				if (!event.getConnection().isOnlineMode()) {
					String ipAddress = event.getConnection().getAddress().getHostString();

					if (ipList.asMap().containsKey(ipAddress)) {
						event.setCancelled(true);
						event.setCancelReason(
								"§c§lRATE LIMIT\n\n§cMuitas tentativas de conexão ao mesmo tempo!\n§7Aguarde 3 segundos para entrar no servidor novamente!");
					} else {
						ipList.put(ipAddress, new JsonObject());
						boolean passBot = checkBot(event);

						if (passBot)
							CommonGeneral.getInstance().debug("The player " + event.getConnection().getName() + " ("
									+ event.getConnection().getAddress().getHostString() + ") has been verified!");
					}
				}

			} catch (Exception ex) {
				event.setCancelled(true);
				event.setCancelReason(
						"§cO servidor não conseguiu fazer as verificações de ip!\n§f\n§c" + CommonConst.DISCORD);
				CommonGeneral.getInstance().getLogger().log(Level.WARNING,
						"An error occured when checking the player " + event.getConnection().getName() + " account!",
						ex);
			}

			event.completeIntent(BungeeMain.getInstance());
		});

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onLogin(LoginEvent event) {
		event.registerIntent(BungeeMain.getInstance());
		CommonGeneral.getInstance().getCommonPlatform().runAsync(() -> {

			try {
//				boolean passMember = loadMember(event);
//
//				if (!passMember) {
//					event.completeIntent(BungeeMain.getInstance());
//					return;
//				}
				loadMember(event);
			} catch (Exception ex) {
				event.setCancelled(true);
				event.setCancelReason("§cSua conta não foi carregada!\n§f\n§c" + CommonConst.DISCORD);
				CommonGeneral.getInstance().getLogger().log(Level.WARNING,
						"An error occured when loading the player " + event.getConnection().getName() + " account!",
						ex);
			}

			event.completeIntent(BungeeMain.getInstance());
		});
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPostLogin(PostLoginEvent event) {
		((BungeeMember) CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId()))
				.setProxiedPlayer(event.getPlayer());
	}

	@EventHandler
	public void onPermissionCheck(PermissionCheckEvent event) {
		CommandSender sender = event.getSender();

		if (sender instanceof ProxiedPlayer) {
			Member member = CommonGeneral.getInstance().getMemberManager()
					.getMember(((ProxiedPlayer) sender).getUniqueId());

			if (member.hasGroupPermission(Group.ADMIN))
				event.setHasPermission(true);
		}
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent event) {
		ProxiedPlayer proxiedPlayer = event.getPlayer();

		BungeeMain.getPlugin().getProxy().getScheduler().runAsync(BungeeMain.getPlugin(), () -> {
			Report report = CommonGeneral.getInstance().getReportManager().getReport(proxiedPlayer.getUniqueId());

			if (report != null)
				report.setOnline(false);

			Member member = CommonGeneral.getInstance().getMemberManager().getMember(proxiedPlayer.getUniqueId());

			if (member != null) {
				CommonGeneral.getInstance().getPlayerData().cacheMember(member.getUniqueId());
				CommonGeneral.getInstance().getMemberManager().unloadMember(member.getUniqueId());

				Clan clan = CommonGeneral.getInstance().getClanManager().getClan(member.getClanUniqueId());

				if (clan != null) {
					if (clan.getOnlineMembers().size() != 0) {
						clan.callEvent(new MemberOnlineEvent(clan, member, false));
					} else {
						CommonGeneral.getInstance().debug("Clan " + clan.getClanName() + " has been unloaded!");
						CommonGeneral.getInstance().getClanManager().unload(clan.getUniqueId());
					}
				}

				member.setLeaveData();
			}
		});
	}

	/**
	 * 
	 * Check if the player is an bot
	 * 
	 * @param event
	 * @return
	 */

	public boolean checkBot(PreLoginEvent event) {
		String ipAddress = event.getConnection().getAddress().getHostString();
		String playerName = event.getConnection().getName();

		if (playerName.toLowerCase().startsWith("mcdrop")) {
			event.setCancelled(true);
			event.setCancelReason(
					"§cO seu ip foi bloqueado de entrar por conter \n\"mcdrop\"!\n\n" + CommonConst.DISCORD);
			blockIp(ipAddress);
			return false;
		}

		BotMember botMember = BungeeMain.getInstance().getBotController().getBotMember(ipAddress);

		if (botMember.isBlocked()) {
			event.setCancelled(true);
			event.setCancelReason("§cO seu ip está bloqueado de entrar!\n§f\n" + CommonConst.DISCORD);
			CommonGeneral.getInstance().debug("O player " + ipAddress + " bloqueado!");
			return false;
		}

		botMember.addName(playerName);

		if (botMember.tooMany()) {
			event.setCancelled(true);
			event.setCancelReason(
					"§cO seu ip está bloqueado de entrar por vários nicknames diferentes!\n§f\n" + CommonConst.DISCORD);

			botMember.block();
			CommonGeneral.getInstance().debug("The address " + ipAddress + " has been blocked for too many nicknames!");
			blockIp(ipAddress);
			return false;
		}

		IpInfo ipInfo = loadIp(ipAddress);

		if (ipInfo == null) {
			event.setCancelled(true);
			event.setCancelReason("§cO servidor não conseguiu!\n§f\n" + CommonConst.DISCORD);
			return false;
		}

		switch (ipInfo.getIpStatus()) {
		case NOT_ALLOWED: {
			event.setCancelled(true);
			event.setCancelReason("§cO seu ip foi bloqueado por não ser confiável!\n\n" + CommonConst.DISCORD);
			CommonGeneral.getInstance()
					.debug("The address " + ipAddress + " has been blocked for isnt a allowed country address!");
			blockIp(ipAddress);
			return false;
		}
		case BLOCKED: {
			event.setCancelled(true);
			event.setCancelReason("§cO seu ip está bloqueado de entrar no servidor!\n\n" + CommonConst.DISCORD);
			CommonGeneral.getInstance().debug("The address " + ipAddress + " has been blocked!");
			blockIp(ipAddress);
			return false;
		}
		default: {
			return true;
		}
		}
	}

	/**
	 * 
	 * Check if the player can login
	 * 
	 * @param event
	 * @return
	 */

	public boolean checkLogin(PreLoginEvent event) {
		System.out.println("§aVerificando a conta do jogador " + event.getConnection().getName() + "...");
		try {
			JsonObject jsonObject = CommonConst.DEFAULT_WEB
					.doRequest(CommonConst.MOJANG_FETCHER + event.getConnection().getName(), Method.GET)
					.getAsJsonObject();

			if (jsonObject.has("name") && jsonObject.has("id"))
				return true;
		} catch (IllegalArgumentException e) {
			return false;
		} catch (Exception e) {
		}

		return false;
	}

	/**
	 * 
	 * Load the member from the database and do all needs check
	 * 
	 * @param event
	 * @return
	 */

	public boolean loadMember(LoginEvent event) {
		String playerName = event.getConnection().getName();
		UUID uniqueId = event.getConnection().getUniqueId();

		BungeeMember member = CommonGeneral.getInstance().getPlayerData().loadMember(uniqueId, BungeeMember.class);
		boolean created = false;

		if (member == null) {
			member = new BungeeMember(playerName, event.getConnection().getUniqueId(),
					!event.getConnection().isOnlineMode() ? AccountType.CRACKED : AccountType.ORIGINAL);

			CommonGeneral.getInstance().getPlayerData().createMember(member);
			CommonGeneral.getInstance().debug("The player " + member.getPlayerName() + " has been saved as "
					+ member.getLoginConfiguration().getAccountType().name());
			created = true;
		} else {
			if (member.getLoginConfiguration().getAccountType() == AccountType.CRACKED) {
				if (!member.getPlayerName().equals(playerName)) {
					event.setCancelled(true);
					event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
							+ "Sua conta está com nickname diferente do original registrado no servidor!\n§f\n"
							+ CommonConst.DISCORD);
					return false;
				}
			}
		}

		member.setJoinData(playerName, event.getConnection().getAddress().getHostString());
		member.setFakeName(member.getPlayerName());
		member.setServerType(ServerType.NONE);
		member.getLoginConfiguration().logOut();

		Ban activeBan = null;

		if (member.getPunishmentHistory() != null)
			activeBan = member.getPunishmentHistory().getActiveBan();

		if (activeBan != null) {
			event.setCancelled(true);
			event.setCancelReason(BungeeMain.getInstance().getPunishManager().getBanMessage(activeBan));
			return false;
		}

		String ipAddress = event.getConnection().getName();

		if (BungeeMain.getInstance().getPunishManager().isIpBanned(ipAddress)) {
			Ban ban = new Ban(Category.CHEATING, member.getUniqueId(), member.getPlayerName(), "CONSOLE", UUID.randomUUID(),
							  Category.CHEATING.getReason(), created ? -1 : System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 7));

			member.getPunishmentHistory().ban(ban);

			CommonGeneral.getInstance().getPlayerData().updateMember(member, "punishmentHistory");
			event.setCancelled(true);
			event.setCancelReason(BungeeMain.getInstance().getPunishManager().getBanMessage(ban));
			return false;
		}

		if (BungeeMain.getInstance().isMaintenceMode()) {
			if (!member.hasGroupPermission(Group.BETA)) {
				event.setCancelled(true);
				event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
						+ "\n§f\n§cO servidor está em modo manutenção\n§f\n§6Mais informação em: §b"
						+ CommonConst.DISCORD);
				return false;
			}
		}

		if (member.getClanUniqueId() != null) {
			Clan clan = CommonGeneral.getInstance().getClanManager().getClan(member.getClanUniqueId());

			if (clan == null) {
				ClanModel clanModel = CommonGeneral.getInstance().getClanData().loadClan(member.getClanUniqueId());

				if (clanModel == null) {
					member.setClanUniqueId(null);
				} else {
					clan = new BungeeClan(clanModel);
					CommonGeneral.getInstance().getClanManager().load(member.getClanUniqueId(), clan);
					CommonGeneral.getInstance().debug("Clan " + clan.getClanName() + " has been loaded!");
				}
			}

			if (clan != null) {
				if (clan.isMember(member.getUniqueId())) {
					clan.updateMember(member);

					clan.callEvent(new MemberOnlineEvent(clan, member, true));
				} else
					member.setClanUniqueId(null);
			}
		}

		Report report = CommonGeneral.getInstance().getReportManager().getReport(uniqueId);

		if (report != null) {
			report.setOnline(true);
			report.setPlayerName(playerName);
		}

		if (member.hasSkin()) {
			if (textureCache.asMap().containsKey(playerName))
				loadTexture(event.getConnection(), textureCache.getIfPresent(playerName));
			else {
				BungeeMember m = member;

				CommonConst.DEFAULT_WEB.doAsyncRequest(
						String.format(CommonConst.SKIN_URL, member.getSkinProfile().getUniqueId()), Method.GET,
						new FutureCallback<JsonElement>() {

							@Override
							public void result(JsonElement result, Throwable error) {
								if (error == null) {
									loadTexture(event.getConnection(), result.getAsJsonObject());
									textureCache.put(playerName, result.getAsJsonObject());
								} else
									m.sendMessage("§cNão foi possível carregar sua skin customizada!");
							}
						});
			}
		}

		member.setOnline(true);
		member.updateTime();

		CommonGeneral.getInstance().getMemberManager().loadMember(member);
		return true;
	}

	/**
	 * 
	 * Remove ip from block
	 * 
	 * @param event
	 */

	@EventHandler(priority = EventPriority.LOWEST)
	public void onIpRemove(IpRemoveEvent event) {
		String ipAddress = event.getIpAddress();

		CommonGeneral.getInstance().getCommonPlatform().runAsync(() -> {
			IpInfo ipInfo = loadIp(ipAddress);

			ipInfo.setIpStatus(IpStatus.ACCEPT);
			BungeeMain.getInstance().getBotController().getBlockedAddress().remove(ipAddress);
		});
	}

	/**
	 * 
	 * Load ip from database or from ip data fetcher
	 * 
	 * @param ipAddress
	 * @return
	 */

	public IpInfo loadIp(String ipAddress) {
		IpInfo ipInfo = ipMap.computeIfAbsent(ipAddress,
				v -> CommonGeneral.getInstance().getIpData().loadIp(ipAddress));

		if (ipInfo == null) {
			try {
				CommonGeneral.getInstance().debug("The ip " + ipAddress + " has been loaded from fetcher!");
				ipInfo = IpFetcher.fetchAddress(ipAddress);
			} catch (FetchAddressException e) {
				e.printStackTrace();
			}

			if (ipInfo == null)
				return null;

			ipMap.put(ipAddress, ipInfo);
			CommonGeneral.getInstance().getIpData().registerIp(ipInfo);
		}

		return ipInfo;
	}

	/**
	 * 
	 * Load the texture in PendingConnection
	 * 
	 * @param connection
	 * @param jsonObject
	 */

	public void loadTexture(PendingConnection connection, JsonObject jsonObject) {
		InitialHandler initialHandler = (InitialHandler) connection;
		LoginResult loginProfile = initialHandler.getLoginProfile();

		LoginResult.Property property = null;

		if (jsonObject.has("properties")) {
			JsonArray jsonArray = jsonObject.get("properties").getAsJsonArray();

			for (int x = 0; x < jsonArray.size(); x++) {
				JsonObject json = (JsonObject) jsonArray.get(x);

				if (json.get("name").getAsString().equalsIgnoreCase("textures")) {
					property = new LoginResult.Property("textures", json.get("value").getAsString(),
							json.get("signature").getAsString());
					break;
				}
			}
		}

		if (loginProfile == null || (loginProfile == null && property == null)) {
			LoginResult loginResult = new LoginResult(connection.getUniqueId().toString().replace("-", ""),
					connection.getName(),
					property == null ? new LoginResult.Property[] {} : new LoginResult.Property[] { property });

			try {
				Class<?> initialHandlerClass = connection.getClass();
				Field profileField = initialHandlerClass.getDeclaredField("loginProfile");
				profileField.setAccessible(true);
				profileField.set(connection, loginResult);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			if (property != null)
				loginProfile.setProperties(new LoginResult.Property[] { property });
		}
	}

	/**
	 * 
	 * Block the ip from future connections in the server
	 * 
	 * @param ipAddress
	 */

	public void blockIp(String ipAddress) {
		IpInfo ipInfo = loadIp(ipAddress);

		if (ipInfo.getIpStatus() == IpStatus.ACCEPT)
			ipInfo.setIpStatus(IpStatus.BLOCKED);

		BungeeMain.getInstance().getBotController().blockIp(ipAddress);
	}

}
