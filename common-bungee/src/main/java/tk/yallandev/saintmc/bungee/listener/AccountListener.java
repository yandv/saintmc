package tk.yallandev.saintmc.bungee.listener;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.bungee.bungee.BungeeClan;
import tk.yallandev.saintmc.bungee.bungee.BungeeMember;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.configuration.LoginConfiguration.AccountType;
import tk.yallandev.saintmc.common.ban.constructor.Ban;
import tk.yallandev.saintmc.common.clan.Clan;
import tk.yallandev.saintmc.common.clan.ClanModel;
import tk.yallandev.saintmc.common.clan.event.member.MemberOnlineEvent;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.permission.RankType;
import tk.yallandev.saintmc.common.report.Report;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.utils.string.NameUtils;
import tk.yallandev.saintmc.common.utils.supertype.FutureCallback;
import tk.yallandev.saintmc.common.utils.web.WebHelper.Method;

public class AccountListener implements Listener {

	private int error;
	private long lastError;

	/*
	 * Change the onlineMode status
	 */

	@EventHandler(priority = EventPriority.NORMAL)
	public void onLogin(PreLoginEvent event) {
		if (event.isCancelled())
			return;

		String playerName = event.getConnection().getName();
		PendingConnection connection = event.getConnection();

		if (error >= 5)
			if (lastError + 15000l > System.currentTimeMillis()) {
				return;
			} else
				error = 0;

		event.registerIntent(BungeeMain.getPlugin());
		ProxyServer.getInstance().getScheduler().runAsync(BungeeMain.getPlugin(), new Runnable() {
			@Override
			public void run() {
				/*
				 * Verify if the player is cracked or premium
				 */
				try {
					CommonGeneral.getInstance().getMojangFetcher().isCracked(playerName, new FutureCallback<Boolean>() {

						@Override
						public void result(Boolean cracked, Throwable error) {

							if (error == null) {
								/*
								 * Change the login status If the onlineMode equals false the cracked player
								 * will able to join or if equals true the cracked player wont able to join
								 */

								connection.setOnlineMode(!cracked);

								CommonConst.DEFAULT_WEB.doAsyncRequest(CommonConst.SKIN_URL + "?name=" + playerName,
										Method.GET, new FutureCallback<JsonElement>() {

											@Override
											public void result(JsonElement result, Throwable error) {
												JsonObject jsonObject = result.getAsJsonObject();

												if (error == null) {
													if (jsonObject.has("properties")) {
														JsonArray jsonArray = jsonObject.get("properties")
																.getAsJsonArray();

														for (int x = 0; x < jsonArray.size(); x++) {
															JsonObject json = (JsonObject) jsonArray.get(x);

															if (json.get("name").getAsString()
																	.equalsIgnoreCase("textures")) {
																try {
																	Class<?> initialHandlerClass = event.getConnection()
																			.getClass();
																	Field loginProfile = initialHandlerClass
																			.getDeclaredField("loginProfile");

																	LoginResult.Property property = new LoginResult.Property(
																			"textures", json.get("value").getAsString(),
																			json.get("signature").getAsString());
																	LoginResult loginResult = new LoginResult(
																			event.getConnection().getUniqueId()
																					.toString(),
																			event.getConnection().getName(),
																			new LoginResult.Property[] { property });

																	loginProfile.setAccessible(true);
																	loginProfile.set(event.getConnection(),
																			loginResult);
																} catch (Exception ex) {

																}
																break;
															}
														}
													}
												}
											}
										});
							} else {
								event.setCancelled(true);
								event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
										+ "\n§f\n§fNão foi possível verificar seu status com a mojang!§f\n§6Mais informação em: §b"
										+ CommonConst.DISCORD);
								error.printStackTrace();
								AccountListener.this.error++;
								lastError = System.currentTimeMillis();
							}

							event.completeIntent(BungeeMain.getPlugin());
						}
					});
				} catch (Exception ex) {
					event.setCancelled(true);
					event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
							+ "\n§f\n§fNão foi possível verificar seu status com a mojang!§f\n§6Mais informação em: §b"
							+ CommonConst.DISCORD);
					event.completeIntent(BungeeMain.getPlugin());

					ex.printStackTrace();

					error++;
					lastError = System.currentTimeMillis();
				}
			}
		});
	}

	/*
	 * Load Member
	 */

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogin(LoginEvent event) {
		if (event.isCancelled())
			return;

		UUID uniqueId = event.getConnection().getUniqueId();
		String playerName = event.getConnection().getName();

		/*
		 * Check if the uniqueId is true
		 */

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

				try {

					/*
					 * Load MemberModel from backend
					 */

					/*
					 * Load Member by playerName to prevent account collision
					 */

					MemberModel basedName = CommonGeneral.getInstance().getPlayerData().loadMember(playerName);

					if (basedName != null) {
						if (basedName.getLoginConfiguration().getAccountType() != AccountType.ORIGINAL) {
							if (!basedName.getPlayerName().equals(playerName)) {
								event.setCancelled(true);
								event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
										+ "\n§f\n§fUma conta já está registrada no servidor com nickname \""
										+ basedName.getPlayerName() + "\"!\n§f\n§6Mais informação em: §b"
										+ CommonConst.DISCORD);
								event.completeIntent(BungeeMain.getPlugin());
								return;
							}

							if (!basedName.getUniqueId().equals(uniqueId)) {
								event.setCancelled(true);
								event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
										+ "\n§f\n§fUma conta já está registrada no servidor com o mesmo nome mas com dados diferentes!\n§f\n§6Mais informação em: §b"
										+ CommonConst.DISCORD);
								event.completeIntent(BungeeMain.getPlugin());
								return;
							}
						}
					}

					/*
					 * Load Member by uniqueId
					 */

					MemberModel memberModel = CommonGeneral.getInstance().getPlayerData().loadMember(uniqueId);

					if (basedName != null && !basedName.getUniqueId().equals(memberModel.getUniqueId())
							&& basedName.getLoginConfiguration().getAccountType() != basedName.getLoginConfiguration()
									.getAccountType()) {
						event.setCancelled(true);
						event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX + "\n§f\n§fUma conta "
								+ NameUtils.formatString(memberModel.getLoginConfiguration().getAccountType().name())
								+ " com o mesmo nick que a sua já está registrada no servidor!\n§f\n§6Mais informação em: §b"
								+ CommonConst.DISCORD);
						event.completeIntent(BungeeMain.getPlugin());
						return;
					}

					Member member = null;

					/*
					 * Create instance of Member using MemberModel from backend
					 */

					if (memberModel == null) {
						member = new BungeeMember(playerName, uniqueId);
						CommonGeneral.getInstance().getPlayerData().createMember(member);
					} else {
						member = new BungeeMember(memberModel);
					}

					/*
					 * Cache the member in redis
					 */

					CommonGeneral.getInstance().getPlayerData().checkCache(uniqueId);

					/*
					 * Start Member
					 */

					member.setJoinData(playerName, ipAddress.getHostString());
					member.updateTime();
					member.setFakeName(member.getPlayerName());
					member.setServerType(ServerType.NONE);

					/*
					 * Save in local storage
					 */

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

				Ban activeBan = null;

				if (member.getPunishmentHistory() != null)
					activeBan = member.getPunishmentHistory().getActiveBan();

				if (activeBan != null) {
					event.setCancelled(true);
					event.setCancelReason(BungeeMain.getInstance().getPunishManager().getBanMessage(activeBan));
					event.completeIntent(BungeeMain.getPlugin());
					CommonGeneral.getInstance().getMemberManager().unloadMember(member.getUniqueId());
					return;
				}

				/*
				 * Start LoginConfiguration of player
				 */

				boolean cracked = !event.getConnection().isOnlineMode();

				if (cracked) {

					/*
					 * Check if player is storage like original and if to dont allow the member join
					 */

					if (member.getLoginConfiguration().getAccountType() == AccountType.ORIGINAL) {
						event.setCancelled(true);
						event.setCancelReason(
								"§4§lMOJANG\n\n§fA conta original logada no servidor é original!\nVocê precisa logar nela como original!\n§f\nCaso esteja no minecraft original, isso pode está acontecendo por causa da nossa conexão com a mojang!\nEntre em contato pelo discord §b"
										+ CommonConst.DISCORD);
						event.completeIntent(BungeeMain.getPlugin());
						CommonGeneral.getInstance().getMemberManager().unloadMember(member.getUniqueId());
						return;
					}

					/*
					 * Logout the Member
					 */

					member.getLoginConfiguration().logOut();

					/*
					 * Register the cracked uniqueId in javascript-coded backend
					 */

					try {
						CommonGeneral.getInstance().getMojangFetcher().registerUuid(playerName, uniqueId);
					} catch (Exception ex) {
						event.setCancelled(true);
						event.setCancelReason("§4§lMOJANG\n\n§fNão foi possível salvar seu id pirata!");
						event.completeIntent(BungeeMain.getPlugin());
						CommonGeneral.getInstance().getMemberManager().unloadMember(member.getUniqueId());
						return;
					}
				}

				/*
				 * Save the account status from Member
				 */

				if (member.getLoginConfiguration().getAccountType() == null
						|| member.getLoginConfiguration().getAccountType() == AccountType.NONE)
					member.getLoginConfiguration().setAccountType(cracked ? AccountType.CRACKED : AccountType.ORIGINAL);

				/*
				 * Check ban of player
				 */

				if (BungeeMain.getInstance().getPunishManager().isIpBanned(ipAddress.getHostString())) {
					Ban ban = new Ban(member.getUniqueId(), "CONSOLE", UUID.randomUUID(), "Conta alternativa",
							System.currentTimeMillis() + (1000 * 60 * 60 * 24
									* (member.getLoginConfiguration().getAccountType() == AccountType.CRACKED ? 14
											: 7)));

					member.getPunishmentHistory().ban(ban);
					CommonGeneral.getInstance().getMemberManager().getMembers().stream()
							.filter(m -> m.hasGroupPermission(Group.TRIAL)).forEach(m -> {
								m.sendMessage(" §4* §cO jogador " + member.getPlayerName() + " foi banido pelo "
										+ ban.getBannedBy() + " por " + ban.getReason() + "!");
							});

					CommonGeneral.getInstance().getPlayerData().updateMember(member, "punishmentHistory");
					event.setCancelled(true);
					event.setCancelReason(BungeeMain.getInstance().getPunishManager().getBanMessage(ban));
					event.completeIntent(BungeeMain.getPlugin());
					CommonGeneral.getInstance().getMemberManager().unloadMember(member.getUniqueId());
					return;
				}

				if (BungeeMain.getInstance().isMaintenceMode()) {
					if (!member.hasGroupPermission(Group.YOUTUBER)) {
						event.setCancelled(true);
						event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
								+ "\n§f\n§cO servidor está em modo manutenção\n§f\n§6Mais informação em: §b"
								+ CommonConst.DISCORD);
						event.completeIntent(BungeeMain.getPlugin());
						CommonGeneral.getInstance().getMemberManager().unloadMember(member.getUniqueId());
						return;
					}
				}

				if (member.getClanUniqueId() != null) {
					Clan clan = CommonGeneral.getInstance().getClanManager().getClan(member.getClanUniqueId());

					if (clan == null) {
						ClanModel clanModel = CommonGeneral.getInstance().getClanData()
								.loadClan(member.getClanUniqueId());

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

				/*
				 * Start report
				 */

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
		/*
		 * Check if the account has been stored locally
		 */

		if (CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId()) == null) {
			event.getPlayer().disconnect(new TextComponent(
					"§4§lCONTA\n\n§fSua conta não foi carregada!\n§6Mais informação em: §b" + CommonConst.WEBSITE));
			return;
		}

		/*
		 * Start BungeeMember
		 */

		BungeeMember member = (BungeeMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		member.setProxiedPlayer(event.getPlayer());

		if (!member.isBdff()) {
			if (member.getLoginConfiguration().getAccountType() == AccountType.ORIGINAL)
				if (member.getPlayerName().toLowerCase().endsWith("bdf")
						|| member.getPlayerName().toLowerCase().endsWith("bdf_")) {

					boolean add = false;

					if (member.getRanks().containsKey(RankType.SAINT)) {
						member.getRanks().put(RankType.SAINT,
								member.getRanks().get(RankType.SAINT) + (1000 * 60 * 60 * 24 * 2));
						add = true;
					} else
						member.getRanks().put(RankType.SAINT, System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 2));

					member.saveRanks();

					if (add)
						member.sendMessage(
								"§aVocê recebeu mais 2 dias de vip " + Tag.SAINT.getPrefix() + " por ter BDF no nick!");
					else
						member.sendMessage(
								"§aVocê recebeu vip " + Tag.SAINT.getPrefix() + " de 2 dias por ter BDF no nick!");
					member.setBdff(true);
				}
		}
	}

	@EventHandler
	public void onPermissionCheck(PermissionCheckEvent event) {
		CommandSender sender = event.getSender();

		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;

			Member member = CommonGeneral.getInstance().getMemberManager().getMember(proxiedPlayer.getUniqueId());

			if (member.hasGroupPermission(Group.DIRETOR))
				event.setHasPermission(true);
		}
	}

	/*
	 * Remove Member account
	 */

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
				}
			}
		});
	}

}
