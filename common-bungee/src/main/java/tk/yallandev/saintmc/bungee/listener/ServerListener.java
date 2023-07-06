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
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.SearchServerEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.bungee.bungee.BungeeMember;
import tk.yallandev.saintmc.bungee.event.server.ServerUpdateEvent;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerManager;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;
import tk.yallandev.saintmc.common.utils.ip.Session;
import tk.yallandev.saintmc.common.utils.string.MessageBuilder;
import tk.yallandev.saintmc.common.utils.string.StringCenter;
import tk.yallandev.saintmc.common.utils.string.StringUtils;
import tk.yallandev.saintmc.common.utils.string.StringUtils.TimeFormat;

@SuppressWarnings("deprecation")
public class ServerListener implements Listener {

	private static final String MOTD_HEADER = StringCenter.centered("§b§lPENTA §f» §awww." + CommonConst.SITE, 127);
	private static final String SERVER_NOT_FOUND = StringCenter.centered("§4§nServidor não encontrado!", 127);

	private static final String[] MOTD_LIST = new String[] { StringCenter.centered("§f§lOPEN §1§lBETA", 127) };

	private ServerManager manager;

	public ServerListener(ServerManager manager) {
		this.manager = manager;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPostLogin(PostLoginEvent event) {
		BungeeMain.getInstance().getServerManager().setTotalMembers(ProxyServer.getInstance().getOnlineCount());
		CommonGeneral.getInstance().getServerData().setTotalMembers(ProxyServer.getInstance().getOnlineCount());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDisconnect(PlayerDisconnectEvent event) {
		BungeeMain.getInstance().getServerManager().setTotalMembers(ProxyServer.getInstance().getOnlineCount() - 1);
		CommonGeneral.getInstance().getServerData().setTotalMembers(ProxyServer.getInstance().getOnlineCount() - 1);
	}

	@EventHandler
	public void onServerKick(ServerKickEvent event) {
		ProxiedPlayer player = event.getPlayer();
		ProxiedServer kickedFrom = manager.getServer(event.getKickedFrom().getName());

		if (kickedFrom == null) {
			player.disconnect(event.getKickReasonComponent());
			return;
		}

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		if (member == null) {
			player.disconnect(event.getKickReasonComponent());
			return;
		}

		ProxiedServer fallbackServer = member.getLoginConfiguration().isLogged()
				? manager.getBalancer(kickedFrom.getServerType().getServerLobby()).next()
				: null;

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
		event.setCancelServer(fallbackServer.getServerInfo());
	}

	@EventHandler
	public void onSearchServer(SearchServerEvent event) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());
		boolean logged = event.getPlayer().getAccountType() == AccountType.CRACKED
				? member.getLoginConfiguration().isLogged()
				: true;

		if (event.getPlayer().getAccountType() == AccountType.CRACKED) {
			String ipAddress = event.getPlayer().getAddress().getHostString();

			if (member.getLoginConfiguration()
					.getAccountType() == tk.yallandev.saintmc.common.account.configuration.LoginConfiguration.AccountType.CRACKED) {

				Session session = member.getLoginConfiguration().getSession(ipAddress);

				if (session != null) {
					if (session.hasExpired()) {
						logged = false;
						member.sendMessage("§cSua sessão expirou!");
						member.getLoginConfiguration().removeSession(ipAddress);
					} else {
						logged = true;
						member.getLoginConfiguration().login(ipAddress);
						member.sendMessage("§aAutenticado automaticamente pelo servidor!");
					}
				}
			}
		}

		Entry<ProxiedServer, ServerType> entry = searchServer(event.getPlayer(), logged, true);

		ProxiedServer server = entry.getKey();

		if (entry.getValue() == ServerType.HUNGERGAMES)
			if (server == null || server.getServerInfo() == null) {
				server = searchServer(event.getPlayer(), logged, false).getKey();
				event.getPlayer().sendMessage("§cNenhum servidor de Comp disponível!");
			}

		if (server == null || server.getServerInfo() == null) {
			event.setCancelled(true);
			event.setCancelMessage("§4§l" + CommonConst.KICK_PREFIX
					+ (event.getPlayer().getAccountType() == AccountType.CRACKED
							? "§4§l" + "\n§f\n§fNenhum servidor de §alogin§f está disponível no momento!\n"
									+ "§f\n§fAcesse nosso discord para mais informações:\n§b" + CommonConst.DISCORD
							: "\n\n§fNenhum servidor de §alobby§f está disponível no momento!\n"
									+ "§f\n§fAcesse nosso discord para mais informações:\n§b" + CommonConst.DISCORD));

			return;
		}

		if (server.isFull()) {
			event.setCancelled(true);
			event.setCancelMessage("§4§l" + CommonConst.KICK_PREFIX
					+ "\n§f\n§fO servidor no qual você foi redirecionado está cheio no momento!\n"
					+ "§f\n§fAcesse nosso discord para mais informações:\n§b" + CommonConst.DISCORD);
			return;
		}

		event.setServer(server.getServerInfo());
		System.out.println(CommonConst.GSON.toJson(server));
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

		if (server.getServerType() == ServerType.LOGIN) {
			if (player.getLoginConfiguration()
					.getAccountType() == tk.yallandev.saintmc.common.account.configuration.LoginConfiguration.AccountType.ORIGINAL
					&& !player.hasGroupPermission(Group.MODPLUS)) {

				ProxiedServer proxiedServer = manager.getBalancer(ServerType.LOBBY).next();

				if (proxiedServer == null || proxiedServer.getServerInfo() == null) {
					event.getPlayer().disconnect("§cNenhum servidor disponível!");
					event.setCancelled(true);
				} else {
					event.getPlayer().connect(proxiedServer.getServerInfo());
				}
			}

			return;
		}

		if (!player.getLoginConfiguration().isLogged()) {
			if (CommonGeneral.getInstance().isLoginServer() ? server.getServerType() == ServerType.LOGIN
					: server.getServerType() == ServerType.LOBBY) {
				return;
			}

			ProxiedServer proxiedServer = manager.getBalancer(ServerType.LOGIN).next();

			if (proxiedServer == null || proxiedServer.getServerInfo() == null) {
				event.getPlayer().disconnect("§cNenhum servidor disponível!");
				event.setCancelled(true);
			} else {
				event.getPlayer().connect(proxiedServer.getServerInfo());
			}
			return;
		}

		String message = "§aSucesso!";

		if (server.isFull() && !player.hasGroupPermission(Group.VIP)) {
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
			serverPing.setDescription(StringCenter.centered(MOTD_HEADER) + "\n"
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
			serverPing.setDescription(MOTD_HEADER + "\n" + MOTD_LIST[CommonConst.RANDOM.nextInt(MOTD_LIST.length)]);
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
					serverPing.setDescription(MOTD_HEADER + "\n" + SERVER_NOT_FOUND);
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

	@EventHandler
	public void onServerUpdate(ServerUpdateEvent event) {
		if (event.getProxiedServer().getServerType() == ServerType.HUNGERGAMES) {
			if ((event.getLastState() == MinigameState.WAITING && event.getState() == MinigameState.STARTING
					&& event.getTime() >= 180) || event.getState() == MinigameState.PREGAME) {
				int time = event.getTime();

				if (time == 60 || time == 120) {
					String[] split = event.getProxiedServer().getServerId().split("\\.");
					String serverId = split.length > 0 ? split[0] : event.getProxiedServer().getServerId();

					String message = "§a§lPARTIDA: §7O §bCOMP-" + serverId.toUpperCase() + "§7 irá iniciar em §b"
							+ StringUtils.formatTime(time, TimeFormat.SHORT) + " §fcom mais de §b"
							+ event.getProxiedServer().getOnlinePlayers() + " jogadores! §aClique aqui§e para jogar!";

					CommonGeneral.getInstance().getMemberManager().broadcast(
							new MessageBuilder(message).setClickEvent(ClickEvent.Action.RUN_COMMAND,
									"/connect " + event.getProxiedServer().getServerId()).create(),
							member -> member.getServerType() != ServerType.HUNGERGAMES
									&& member.getServerType() != ServerType.EVENTO);
				}
			}
		}
	}

	private String getServerIp(PendingConnection con) {
		if (con == null || con.getVirtualHost() == null)
			return "";

		return con.getVirtualHost().getHostName().toLowerCase();
	}

}
