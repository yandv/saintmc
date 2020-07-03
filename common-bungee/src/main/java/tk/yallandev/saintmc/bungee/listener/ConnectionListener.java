package tk.yallandev.saintmc.bungee.listener;

import java.util.Random;
import java.util.UUID;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.PlayerInfo;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer.AccountType;
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
import tk.yallandev.saintmc.common.server.ServerManager;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;
import tk.yallandev.saintmc.common.utils.string.StringCenter;

public class ConnectionListener implements Listener {

	private String[] motdList = new String[] { "§b" + CommonConst.DISCORD, "§b" + CommonConst.WEBSITE };
	private ServerManager manager;

	public ConnectionListener(ServerManager manager) {
		this.manager = manager;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onServerKick(ServerKickEvent event) {
		ProxiedPlayer player = event.getPlayer();

		/*
		 * Servidor que ele foi desconectado
		 */

		ProxiedServer kickedFrom = manager.getServer(event.getKickedFrom().getName());

		/*
		 * Verifica se o servidor é nulo, caso seja, wtf / kicka o player
		 */

		if (kickedFrom == null) {
			player.disconnect(event.getKickReasonComponent());
			return;
		}

		/*
		 * Se o servidor for lobby, desconectar o player do servidor!
		 */

		ProxiedServer fallbackServer = manager.getBalancer(kickedFrom.getServerType().getServerLobby()).next();

		if (kickedFrom.getServerType() == ServerType.HUNGERGAMES) {

		}

		/*
		 * Caso o fallback seja nulo, desconectar o player, pois não haverá servidor
		 * para ele entrar
		 */

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
//
//		if (!fallbackServer.containsPlayer(player.getUniqueId())) {
//			event.setCancelServer(fallbackServer.getServerInfo());
//		}
	}

	@EventHandler
	public void onSearchServer(SearchServerEvent event) {
		ProxiedServer server = searchServer(event.getPlayer());

		if (server == null || server.getServerInfo() == null) {
			event.setCancelled(true);
			event.setCancelMessage("§4§l" + CommonConst.KICK_PREFIX
					+ (event.getPlayer().getAccountType() == AccountType.CRACKED ? "§4§l"
							+ "\n§f\n§fNenhum servidor de §alogin§f está disponível no momento!\n§f\n§6Acesse nosso discord para mais informações:\n§b"
							+ CommonConst.DISCORD
							: "\n\n§fNenhum servidor de §alobby§f está disponível no momento!\n§f\n§6Acesse nosso discord para mais informações:\n§b"
									+ CommonConst.DISCORD));
			return;
		}

		if (server.getServerType() == ServerType.LOGIN)
			if (server.isFull()) {
				event.setCancelled(true);
				event.setCancelMessage("§4§l" + CommonConst.KICK_PREFIX
						+ "\n§f\n§fO servidor de §alogin§f está cheio no momento!\n§f\n§6Acesse nosso discord para mais informações:\n§b"
						+ CommonConst.DISCORD);
				return;
			}

		event.setServer(server.getServerInfo());
	}

	private ProxiedServer searchServer(ProxiedPlayer player) {
		String serverId = getServerIp(player.getPendingConnection());

		AccountType accountType = player.getAccountType();

		if (accountType != AccountType.PREMIUM)
			return manager
					.getBalancer(CommonGeneral.getInstance().isLoginServer() ? ServerType.LOGIN : ServerType.LOBBY)
					.next();

		if (serverId.toLowerCase().toLowerCase().contains("hg"))
			return manager.getBalancer(ServerType.HUNGERGAMES).next();

		return manager.getServer(serverId) == null ? manager.getBalancer(ServerType.LOBBY).next()
				: manager.getServer(serverId);
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

		if (event.isCancelled()) {
			if (event.getPlayer().getServer() == null || event.getPlayer().getServer().getInfo() == null)
				event.getPlayer().disconnect(message);
			else
				player.sendMessage(message);
		} else {
			event.setTarget(server.getServerInfo());
		}
	}

	@EventHandler(priority = 127)
	public void onProxyPing(ProxyPingEvent event) {
		String serverIp = getServerIp(event.getConnection());
		ProxiedServer server = manager.getServer(serverIp);
		ServerPing serverPing = event.getResponse();

		if (server == null) {
			serverPing.getPlayers().setMax(ProxyServer.getInstance().getOnlineCount() + 1);
			serverPing.getPlayers().setOnline(ProxyServer.getInstance().getOnlineCount());
			serverPing.getPlayers()
					.setSample(new PlayerInfo[] { new PlayerInfo("§e" + CommonConst.WEBSITE, UUID.randomUUID()) });
			serverPing.setDescription("      §f﹄ §6§lSaint§f§lMC §f| §eMinecraft Network §7(1.7-.12) §f﹃\n"
					+ StringCenter.centered(motdList[new Random().nextInt(motdList.length)], 127));
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
					serverPing.getPlayers().setMax(ProxyServer.getInstance().getOnlineCount() + 1);
					serverPing.getPlayers().setOnline(ProxyServer.getInstance().getOnlineCount());
					serverPing.getPlayers().setSample(
							new PlayerInfo[] { new PlayerInfo("§e" + CommonConst.WEBSITE, UUID.randomUUID()) });
					serverPing.setDescription("    §f﹄ §6§lSaint§f§lMC §f| §eMinecraft Network §7(1.7-.12) §f﹃\n"
							+ StringCenter.centered("§4§nServidor não encontrado!", 127));
				}

				event.completeIntent(BungeeMain.getPlugin());
			}

		});
	}

	private String getServerIp(PendingConnection con) {
		if (con == null || con.getVirtualHost() == null)
			return "";

		return con.getVirtualHost().getHostName().toLowerCase();
	}

}
