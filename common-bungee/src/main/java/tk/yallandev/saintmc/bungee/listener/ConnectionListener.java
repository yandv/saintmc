package tk.yallandev.saintmc.bungee.listener;

import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.UUID;

import net.md_5.bungee.api.AccountType;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.PlayerInfo;
import net.md_5.bungee.api.ServerPing.Protocol;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.SearchServerEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.bungee.bungee.BungeeMember;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.profile.Profile;
import tk.yallandev.saintmc.common.server.ServerManager;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;
import tk.yallandev.saintmc.common.utils.string.StringCenter;

public class ConnectionListener implements Listener {

	private String[] motdList = new String[] { "§b" + CommonConst.DISCORD, "§b" + CommonConst.WEBSITE,
			"§6Bem vindo ao novo!" };
	private ServerManager manager;

	public ConnectionListener(ServerManager manager) {
		this.manager = manager;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onServerKick(ServerKickEvent event) {
		ProxiedPlayer player = event.getPlayer();
		ProxiedServer kickedFrom = manager.getServer(event.getKickedFrom().getName());

		if (kickedFrom == null) {
			player.disconnect(event.getKickReasonComponent());
			return;
		}

		ProxiedServer fallbackServer = manager.getBalancer(kickedFrom.getServerType().getServerLobby()).next();

		if (fallbackServer == null || fallbackServer.getServerInfo() == null) {
			event.getPlayer().disconnect(event.getKickReasonComponent());
			return;
		}

		if (kickedFrom.getServerType() == fallbackServer.getServerType()) {
			player.disconnect(event.getKickReasonComponent());
			return;
		}

		String message = event.getKickReason();

		for (String m : message.split("\n")) {
			player.sendMessage(TextComponent.fromLegacyText(m.replace("\n", "")));
		}

		event.setCancelled(true);
	}

	@EventHandler
	public void onSearchServer(SearchServerEvent event) {
		boolean logged = event.getPlayer().getAccountType() == AccountType.CRACKED ? CommonGeneral.getInstance()
				.getMemberManager().getMember(event.getPlayer().getUniqueId()).getLoginConfiguration().isLogged()
				: true;
		Entry<ProxiedServer, ServerType> entry = searchServer(event.getPlayer(), logged, true);

		ProxiedServer server = entry.getKey();

		if (entry.getValue() == ServerType.HUNGERGAMES)
			if (server == null || server.getServerInfo() == null) {
				server = searchServer(event.getPlayer(), logged, false).getKey();
				event.getPlayer().sendMessage("§cNenhum servidor de Hungergames disponível!");
			}

		if (server == null || server.getServerInfo() == null) {
			event.setCancelled(true);
			event.setCancelMessage("§4§l" + CommonConst.KICK_PREFIX
					+ (event.getPlayer().getAccountType() == AccountType.CRACKED
							? "§4§l" + "\n§f\n§fNenhum servidor de §alogin§f está disponível no momento!\n"
									+ "§f\n§6Acesse nosso discord para mais informações:\n§b" + CommonConst.DISCORD
							: "\n\n§fNenhum servidor de §alobby§f está disponível no momento!\n"
									+ "§f\n§6Acesse nosso discord para mais informações:\n§b" + CommonConst.DISCORD));

			return;
		}

//		if (server.getServerType() == ServerType.LOGIN) {
//			Member member = CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());
//
//			if (member.getLoginConfiguration().hasSession(member.getLastIpAddress())) {
//				member.getLoginConfiguration().login(member.getLastIpAddress());
//
//				ProxiedServer lobbyServer = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.LOBBY)
//						.next();
//
//				if (lobbyServer != null)
//					server = lobbyServer;
//			}
//		}

		if (server.isFull()) {
			event.setCancelled(true);
			event.setCancelMessage("§4§l" + CommonConst.KICK_PREFIX
					+ "\n§f\n§fO servidor no qual você foi redirecionado está cheio no momento!\n"
					+ "§f\n§6Acesse nosso discord para mais informações:\n§b" + CommonConst.DISCORD);
			return;
		}

		event.setServer(server.getServerInfo());
	}

	/*
	 * ServerConnectRequest
	 */

	@EventHandler
	public void onServerConnect(ServerConnectEvent event) {
		BungeeMember player = (BungeeMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		if (player == null)
			return;

		ProxiedServer server = manager.getServer(event.getTarget().getName());

		if (server.getServerType() == ServerType.SCREENSHARE) {
			if (player.isScreensharing() || player.hasGroupPermission(Group.MODGC))
				player.sendMessage("§aVocê foi enviado para Screenshare!");
			else
				event.setCancelled(true);

			return;
		} else if (server.getServerType() == ServerType.LOGIN) {
			if (player.getLoginConfiguration()
					.getAccountType() == tk.yallandev.saintmc.common.account.configuration.LoginConfiguration.AccountType.ORIGINAL
					&& !player.hasGroupPermission(Group.MODGC))
				event.setCancelled(true);

			return;
		}

		if (!player.getLoginConfiguration().isLogged()) {
			if (CommonGeneral.getInstance().isLoginServer() ? server.getServerType() == ServerType.LOGIN
					: server.getServerType() == ServerType.LOBBY) {
				return;
			}

			event.setCancelled(true);
			return;
		}

		String message = "§aSucesso!";

		if (server.isFull() && !player.hasGroupPermission(Group.LIGHT)) {
			event.setCancelled(true);
			message = "§cO servidor está cheio!";
		}

		if (event.isCancelled())
			if (event.getPlayer().getServer() == null || event.getPlayer().getServer().getInfo() == null)
				event.getPlayer().disconnect(message);
			else
				player.sendMessage(message);
		else
			event.setTarget(server.getServerInfo());
	}

	@EventHandler(priority = 127)
	public void onProxyPing(ProxyPingEvent event) {
		ServerPing serverPing = event.getResponse();

		if (BungeeMain.getInstance().isMaintenceMode()) {
			serverPing.setVersion(new Protocol("§cMantencao!", -1));
			serverPing.getPlayers()
					.setSample(new PlayerInfo[] { new PlayerInfo("§e" + CommonConst.WEBSITE, UUID.randomUUID()) });
			serverPing.setDescription("      §f﹄ §6§lSaint§f§lMC §f| §eMinecraft Network §7(1.7-1.15) §f﹃\n"
					+ StringCenter.centered("§cO servidor está em manutenção!", 127));
			return;
		}

		String serverIp = getServerIp(event.getConnection());
		ProxiedServer server = manager.getServer(serverIp);

		serverPing.getPlayers().setMax(900);
		serverPing.getPlayers().setOnline(ProxyServer.getInstance().getOnlineCount());

		if (server == null) {
			serverPing.getPlayers()
					.setSample(new PlayerInfo[] { new PlayerInfo("§e" + CommonConst.WEBSITE, UUID.randomUUID()) });
			serverPing.setDescription("      §f﹄ §6§lSaint§f§lMC §f| §eMinecraft Network §7(1.7-1.15) §f﹃\n"
					+ StringCenter.centered(motdList[CommonConst.RANDOM.nextInt(motdList.length)], 127));
			return;
		}

		event.registerIntent(BungeeMain.getPlugin());
		server.getServerInfo().ping(new Callback<ServerPing>() {

			@Override
			public void done(ServerPing realPing, Throwable throwable) {
				if (throwable == null) {
					serverPing.getPlayers().setMax(realPing.getPlayers().getMax());
					serverPing.getPlayers().setOnline(realPing.getPlayers().getOnline());
					serverPing.setDescription(realPing.getDescription());
				} else {
					serverPing.getPlayers().setSample(
							new PlayerInfo[] { new PlayerInfo("§e" + CommonConst.WEBSITE, UUID.randomUUID()) });
					serverPing.setDescription("    §f﹄ §6§lSaint§f§lMC §f| §eMinecraft Network §7(1.7-1.15) §f﹃\n"
							+ StringCenter.centered("§4§nServidor não encontrado!", 127));
				}

				event.completeIntent(BungeeMain.getPlugin());
			}

		});
	}

	private Entry<ProxiedServer, ServerType> searchServer(ProxiedPlayer player, boolean logged, boolean minigame) {
		String serverId = getServerIp(player.getPendingConnection());

		if (!logged)
			return new AbstractMap.SimpleEntry<>(
					manager.getBalancer(
							CommonGeneral.getInstance().isLoginServer() ? ServerType.LOGIN : ServerType.LOBBY).next(),
					CommonGeneral.getInstance().isLoginServer() ? ServerType.LOGIN : ServerType.LOBBY);

		if (minigame)
			if (serverId.toLowerCase().startsWith("hg"))
				return new AbstractMap.SimpleEntry<>(manager.getBalancer(ServerType.HUNGERGAMES).next(),
						ServerType.HUNGERGAMES);

		return new AbstractMap.SimpleEntry<>(
				manager.getServer(serverId) == null ? manager.getBalancer(ServerType.LOBBY).next()
						: manager.getServer(serverId),
				manager.getServer(serverId) == null ? ServerType.LOBBY : manager.getServer(serverId).getServerType());
	}

	private String getServerIp(PendingConnection con) {
		if (con == null || con.getVirtualHost() == null)
			return "";

		return con.getVirtualHost().getHostName().toLowerCase();
	}

}
