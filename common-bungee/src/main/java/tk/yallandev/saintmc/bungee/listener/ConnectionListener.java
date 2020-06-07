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
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerManager;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;
import tk.yallandev.saintmc.common.utils.string.StringCenter;

public class ConnectionListener implements Listener {

	private String[] motdList = new String[] { "§bdiscord.saintmc.com.br", "§bsaintmc.com.br" };
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

		if (!fallbackServer.containsPlayer(player.getUniqueId())) {
			event.setCancelServer(fallbackServer.getServerInfo());
		}
	}

	@EventHandler
	public void onSearchServer(SearchServerEvent event) {
		String serverId = getServerIp(event.getPlayer().getPendingConnection());

		AccountType accountType = event.getPlayer().getAccountType();
		ProxiedServer server = manager.getServer(serverId) == null ? manager.getBalancer(
				accountType == AccountType.CRACKED && CommonGeneral.getInstance().isLoginServer() ? ServerType.LOGIN
						: ServerType.LOBBY)
				.next() : manager.getServer(serverId);

		if (server == null || server.getServerInfo() == null) {
			event.setCancelled(true);
			event.setCancelMessage(accountType == AccountType.CRACKED ? "§4§l" + CommonConst.KICK_PREFIX
					+ "\n§f\n§fO servidor de §alogin§f está cheio no momento!\n§f\n§6Acesse nosso discord para mais informações:\n§b"
					+ CommonConst.DISCORD
					: "§4§l" + CommonConst.KICK_PREFIX
							+ "\n\n§fNenhum servidor de §alobby§f está disponível no momento!\n§f\n§6Acesse nosso discord para mais informações:\n§b"
							+ CommonConst.DISCORD);
			return;
		}

		event.setServer(server.getServerInfo());
	}

	/*
	 * ServerConnectRequest
	 */
	
	@EventHandler
	public void onServerConnect(ServerConnectEvent event) {
		Member player = CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());

		if (player == null)
			return;
		
		ProxiedServer server = manager.getServer(event.getTarget().getName());

		if (!player.getLoginConfiguration().isLogged()) {
			if (server.getServerType() == ServerType.LOGIN
					|| (CommonGeneral.getInstance().isLoginServer() && server.getServerType() == ServerType.LOBBY)) {
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
		
//		if (!server.canBeSelected() && !player.hasGroupPermission(Group.BUILDER)) {
//			event.setCancelled(true);
//			message = "§cO servidor está disponivel somente para a equipe!";
//		}

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
							new PlayerInfo[] { new PlayerInfo("§cServidor não encontrado!", UUID.randomUUID()) });
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

		String s = con.getVirtualHost().getHostName().toLowerCase();

		if (s.isEmpty())
			return "";

		for (String str : new String[] { "proxy." })
			s = s.replace(str, "");

		return s;
	}

}
