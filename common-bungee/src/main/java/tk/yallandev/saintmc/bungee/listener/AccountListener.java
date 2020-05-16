package tk.yallandev.saintmc.bungee.listener;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.util.UUID;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.http.HttpClient;
import net.md_5.bungee.netty.ChannelWrapper;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.bungee.account.BungeeMember;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.configuration.LoginConfiguration.AccountType;
import tk.yallandev.saintmc.common.ban.constructor.Ban;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.report.Report;
import tk.yallandev.saintmc.common.server.ServerType;

public class AccountListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogin(PreLoginEvent event) {
		String playerName = event.getConnection().getName();

		if (playerName == null || playerName.isEmpty()) {
			event.setCancelled(true);
			event.setCancelReason(
					"§4§lCONTA\n§f\n§fNão foi possível encontrar o seu nickname/id!\n§6Mais informação em: §b"
							+ CommonConst.WEBSITE);
			return;
		}

		if (playerName.toLowerCase().startsWith("mcdrop")) {
			event.setCancelled(true);
			event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
					+ "\n§f\n§fPara evitar §cataques de bot§f, bloqueamos todos os nicks que possuam §c\"mcdrop\"§f\n§6Mais informação em: §b"
					+ CommonConst.DISCORD);
			return;
		}

		String ipAddress = event.getConnection().getAddress().getHostString();

		PendingConnection connection = event.getConnection();

		event.registerIntent(BungeeMain.getPlugin());
		ProxyServer.getInstance().getScheduler().runAsync(BungeeMain.getPlugin(), new Runnable() {
			@Override
			public void run() {

				try {
					boolean cracked = CommonGeneral.getInstance().getMojangFetcher().isCracked(playerName);

					connection.setOnlineMode(!cracked);
				} catch (Exception ex) {
					event.setCancelled(true);
					event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
							+ "\n§f\n§fNão foi possível checar o seu nome!§f\n§6Mais informação em: §b"
							+ CommonConst.DISCORD);
					event.completeIntent(BungeeMain.getPlugin());
					return;
				}

				InitialHandler initialHandler = (InitialHandler) event.getConnection();

				ChannelWrapper channelWrapper = null;

				try {
					Field field = initialHandler.getClass().getDeclaredField("ch");
					field.setAccessible(true);
					channelWrapper = (ChannelWrapper) field.get(initialHandler);
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
						| IllegalAccessException e) {
					e.printStackTrace();
				}

				try {
					HttpClient.get(CommonConst.MOJANG_FETCHER + "session/?ip=" + URLEncoder.encode(ipAddress, "UTF-8"),
							channelWrapper.getHandle().eventLoop(), new Callback<String>() {
								@Override
								public void done(String result, Throwable error) {
									if (error == null) {
										JsonObject jsonObject = (JsonObject) JsonParser.parseString(result);

										if (!jsonObject.get("allow").getAsBoolean()) {
											event.setCancelled(true);
											event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
													+ "\n§f\n§cSeu endereço de ip foi bloqueado!" + "\n\n§fMotivo: §7"
													+ jsonObject.get("message").getAsString()
													+ "\n§f\n§6Mais informação em: §b" + CommonConst.DISCORD);
											event.completeIntent(BungeeMain.getPlugin());

											CommonGeneral.getInstance().debug("Blocked  " + ipAddress + " address!");
											return;
										}

										event.completeIntent(BungeeMain.getPlugin());
									} else {
										event.setCancelled(true);
										event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
												+ "\n§f\n§fNão foi possível verificar o seu ip!§f\n§6Mais informação em: §b"
												+ CommonConst.DISCORD);
										event.completeIntent(BungeeMain.getPlugin());
									}
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

//				try {
//					String url = CommonConst.MOJANG_FETCHER + "session/?ip=" + URLEncoder.encode(ipAddress, "UTF-8");
//					
//					System.out.println(url);
//					
//					HttpGet httpGet = new HttpGet(url);
//					
//					httpGet.addHeader("Content-type", "application/json");
//					
//					CloseableHttpResponse response = CommonConst.HTTPCLIENT.execute(httpGet);
//					
//					String json = EntityUtils.toString(response.getEntity());
//					JsonObject jsonObject = (JsonObject) JsonParser.parseString(json);
//					
//					if (!jsonObject.get("allow").getAsBoolean()) {
//						event.setCancelled(true);
//						event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
//								+ "\n§f\n§cSeu endereço de ip foi bloqueado!"
//								+ "\n\n§fMotivo: §7" + jsonObject.get("message").getAsString()
//								+ "\n§f\n§6Mais informação em: §b"
//								+ CommonConst.DISCORD);
//						event.completeIntent(BungeeMain.getPlugin());
//						
//						CommonGeneral.getInstance().debug("Blocked  " + ipAddress + " address!");
//						return;
//					}
//					
//				} catch (Exception ex) {
//					event.setCancelled(true);
//					event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
//							+ "\n§f\n§fNão foi possível verificar o seu ip!§f\n§6Mais informação em: §b"
//							+ CommonConst.DISCORD);
//					event.completeIntent(BungeeMain.getPlugin());
//					
//					ex.printStackTrace();
//					return;
//				}
//
//				event.completeIntent(BungeeMain.getPlugin());
			}
		});
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogin(LoginEvent event) {
		UUID uniqueId = event.getConnection().getUniqueId();
		String playerName = event.getConnection().getName();

		if (uniqueId == null) {
			event.setCancelled(true);
			event.setCancelReason("§4§lCONTA\n§f\n§fNão foi possível encontrar o seu id!\n§6Mais informação em: §b"
					+ CommonConst.WEBSITE);
			return;
		}

		InetSocketAddress ipAddress = event.getConnection().getAddress();

		event.registerIntent(BungeeMain.getPlugin());
		ProxyServer.getInstance().getScheduler().runAsync(BungeeMain.getPlugin(), new Runnable() {
			@Override
			public void run() {
				CommonGeneral.getInstance().debug("Loading " + uniqueId + " (" + playerName + ") account!");

				String memberName = CommonGeneral.getInstance().getPlayerData().checkNickname(playerName);

				System.out.println(memberName);

				if (memberName != null && !memberName.equals(playerName)) {
					event.setCancelled(true);
					event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
							+ "\n§f\n§fUma conta já está registrada no servidor com nickname \"" + memberName
							+ "\"!\n§f\n§6Mais informação em: §b" + CommonConst.DISCORD);
					event.completeIntent(BungeeMain.getPlugin());
					return;
				}

				try {
					MemberModel memberModel = CommonGeneral.getInstance().getPlayerData().loadMember(uniqueId);
					Member member = null;

					if (memberModel == null) {
						member = new BungeeMember(playerName, uniqueId);
						CommonGeneral.getInstance().getPlayerData().saveMember(member);
					} else {
						member = new BungeeMember(memberModel);
					}

					CommonGeneral.getInstance().getPlayerData().checkCache(uniqueId);
					member.setJoinData(playerName, ipAddress.getHostString());
					member.updateTime();
					member.setFakeName(member.getPlayerName());
					member.setServerType(ServerType.NONE);
					CommonGeneral.getInstance().getMemberManager().loadMember(member);
				} catch (Exception ex) {
					event.setCancelled(true);
					event.setCancelReason("§4§lCONTA\n§f\n§fSua conta não foi carregada!");
					event.completeIntent(BungeeMain.getPlugin());
					ex.printStackTrace();
					return;
				}

				CommonGeneral.getInstance()
						.debug("The account of " + uniqueId + " (" + playerName + ") has been loaded!");

				Member member = CommonGeneral.getInstance().getMemberManager().getMember(uniqueId);

				if (BungeeMain.getInstance().getPunishManager().isIpBanned(ipAddress.getHostString())) {
					Ban ban = new Ban(member.getUniqueId(), "CONSOLE", UUID.randomUUID(), "Conta alternativa", -1l);

					member.getPunishmentHistory().ban(ban);
					CommonGeneral.getInstance().getMemberManager().getMembers().stream()
							.filter(m -> m.hasGroupPermission(Group.TRIAL)).forEach(m -> {
								m.sendMessage(" §4* §cO jogador " + member.getPlayerName() + " foi banido pelo §a"
										+ ban.getBannedBy() + " por " + ban.getReason() + "!");
							});

					CommonGeneral.getInstance().getPlayerData().updateMember(member, "punishmentHistory");
				}

				Ban activeBan = null;

				if (member.getPunishmentHistory() != null)
					activeBan = member.getPunishmentHistory().getActiveBan();

				if (activeBan != null) {
					event.setCancelled(true);
					event.setCancelReason(BungeeMain.getInstance().getPunishManager().getBanMessage(activeBan));
					event.completeIntent(BungeeMain.getPlugin());
					return;
				}

				boolean cracked = CommonGeneral.getInstance().getMojangFetcher().isCracked(playerName);

				if (cracked) {
					member.getLoginConfiguration().logOut();
					CommonGeneral.getInstance().getMojangFetcher().registerUuid(playerName, uniqueId);
				}

				if (member.getLoginConfiguration().getAccountType() == AccountType.NONE)
					member.getLoginConfiguration().setAccountType(cracked ? AccountType.CRACKED : AccountType.ORIGINAL);

				Report report = CommonGeneral.getInstance().getReportManager().getReport(uniqueId);

				if (report != null) {
					report.setOnline(true);
					report.setPlayerName(playerName);
				}

				event.completeIntent(BungeeMain.getPlugin());
			}
		});
	}

	@EventHandler(priority = -127)
	public void onPostLoginCheck(PostLoginEvent event) {
		if (CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId()) == null) {
			event.getPlayer().disconnect(new TextComponent(
					"§4§lCONTA\n\n§fSua conta não foi carregada!\n§6Mais informação em: §b" + CommonConst.WEBSITE));
		}

		BungeeMember member = (BungeeMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());
		member.setProxiedPlayer(event.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerDisconnectEvent event) {
		ProxiedPlayer player = event.getPlayer();
		removePlayer(player, player.getAddress().getHostString());
	}

	private void removePlayer(ProxiedPlayer proxied, String address) {
		BungeeMain.getPlugin().getProxy().getScheduler().runAsync(BungeeMain.getPlugin(), new Runnable() {

			@Override
			public void run() {
				Report report = CommonGeneral.getInstance().getReportManager().getReport(proxied.getUniqueId());

				if (report != null)
					report.setOnline(false);

				Member member = CommonGeneral.getInstance().getMemberManager().getMember(proxied.getUniqueId());

				if (member != null) {
					member.setLeaveData();

					UUID uniqueId = proxied.getUniqueId();

					CommonGeneral.getInstance().getPlayerData().cacheMember(uniqueId);
					CommonGeneral.getInstance().getMemberManager().unloadMember(uniqueId);
				}
			}
		});
	}

}
