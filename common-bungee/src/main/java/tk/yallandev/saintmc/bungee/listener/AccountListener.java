package tk.yallandev.saintmc.bungee.listener;

import java.net.InetSocketAddress;
import java.util.UUID;

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
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
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
import tk.yallandev.saintmc.common.utils.string.NameUtils;

public class AccountListener implements Listener {

	/*
	 * Change the onlineMode status
	 */

//	@EventHandler(priority = EventPriority.NORMAL)
//	public void onLogin(PreLoginEvent event) {
//		if (event.isCancelled())
//			return;
//
//		String playerName = event.getConnection().getName();
//
//		PendingConnection connection = event.getConnection();
//
//		event.registerIntent(BungeeMain.getPlugin());
//		ProxyServer.getInstance().getScheduler().runAsync(BungeeMain.getPlugin(), new Runnable() {
//			@Override
//			public void run() {
//				/*
//				 * Verify if the player is cracked or premium
//				 */
//
//				try {
//					boolean cracked = CommonGeneral.getInstance().getMojangFetcher().isCracked(playerName);
//
//					/*
//					 * Change the login status If the onlineMode equals false the cracked player
//					 * will able to join or if equals true the cracked player wont able to join
//					 */
//
//					connection.setOnlineMode(!cracked);
//					CommonGeneral.getInstance().debug("The connection of " + event.getConnection().getName() + " is "
//							+ (cracked ? "Cracked" : "Premium"));
//				} catch (Exception ex) {
//					event.setCancelled(true);
//					event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
//							+ "\n§f\n§fNão foi possível checar o seu nome!§f\n§6Mais informação em: §b"
//							+ CommonConst.DISCORD);
//				}
//
//				event.completeIntent(BungeeMain.getPlugin());
//			}
//		});
//	}

	/*
	 * Load Member
	 */

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogin(LoginEvent event) {
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
					 * Load Member by uniqueId to prevent account collision
					 */

					MemberModel basedName = CommonGeneral.getInstance().getPlayerData().loadMember(playerName);

					if (basedName != null && !basedName.getPlayerName().equals(playerName)) {
						event.setCancelled(true);
						event.setCancelReason("§4§l" + CommonConst.KICK_PREFIX
								+ "\n§f\n§fUma conta já está registrada no servidor com nickname \""
								+ basedName.getPlayerName() + "\"!\n§f\n§6Mais informação em: §b"
								+ CommonConst.DISCORD);
						event.completeIntent(BungeeMain.getPlugin());
						return;
					}

					/*
					 * Load Member by uniqueId
					 */

					MemberModel memberModel = CommonGeneral.getInstance().getPlayerData().loadMember(uniqueId);

					if (basedName != null && basedName.getLoginConfiguration().getAccountType() != basedName
							.getLoginConfiguration().getAccountType()) {
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

				/*
				 * Check ban of player
				 */

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
						return;
					}
				}

				/*
				 * Save the account status from Member
				 */

				if (member.getLoginConfiguration().getAccountType() == AccountType.NONE)
					member.getLoginConfiguration().setAccountType(cracked ? AccountType.CRACKED : AccountType.ORIGINAL);

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
		}

		/*
		 * Start BungeeMember
		 */

		BungeeMember member = (BungeeMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());
		member.setProxiedPlayer(event.getPlayer());
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

					UUID uniqueId = proxied.getUniqueId();

					CommonGeneral.getInstance().getPlayerData().cacheMember(uniqueId);
					CommonGeneral.getInstance().getMemberManager().unloadMember(uniqueId);
				}
			}
		});
	}

}
