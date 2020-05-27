package tk.yallandev.saintmc.bungee.listener;

import java.util.Random;
import java.util.UUID;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.PlayerInfo;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.ServerPing.Protocol;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.configuration.LoginConfiguration.AccountType;
import tk.yallandev.saintmc.common.server.ServerManager;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.BattleServer;
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

		BattleServer kickedFrom = manager.getServer(event.getKickedFrom().getName());

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

		if (kickedFrom.getServerType().toString().contains("LOBBY")) {
			player.disconnect(event.getKickReasonComponent());
			return;
		}

		/*
		 * Servidor de redirecionamento (fallback server), que serão os lobbies
		 */

		BattleServer fallbackServer = manager.getBalancer(kickedFrom.getServerType().getServerLobby()).next();

		if (kickedFrom.getServerType() == ServerType.HUNGERGAMES) {

			/*
			 * Mas caso o servidor seja um HG, será redirecionado para outro!
			 */

			/*
			 * BattleServer hungerGames =
			 * manager.getBalancer(ServerType.HUNGERGAMES).next();
			 * 
			 * if (hungerGames != null && hungerGames.getServerInfo() != null &&
			 * hungerGames.isFull()) fallbackServer = hungerGames;
			 */
		}

		/*
		 * Caso o fallback seja nulo, desconectar o player, pois não haverá servidor
		 * para ele entrar
		 */

		if (fallbackServer == null || fallbackServer.getServerInfo() == null) {
			event.getPlayer().disconnect(event.getKickReasonComponent());
			return;
		}

		String message = event.getKickReason();

		for (String m : message.split("\n")) {
			player.sendMessage(TextComponent.fromLegacyText(m.replace("\n", "")));
		}

		event.setCancelled(true);

		if (!fallbackServer.containsPlayer(player.getUniqueId()))
			event.setCancelServer(fallbackServer.getServerInfo());
	}

	@EventHandler
	public void onServerConnect(ServerConnectEvent event) {
		Member player = CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());

		if (player == null)
			return;

		BattleServer server;

		if (event.getTarget() == null) {
			server = manager.getBalancer(ServerType.LOBBY).next();

			if (server == null || server.getServerInfo() == null) {
				event.setCancelled(true);
				event.getPlayer().disconnect(TextComponent.fromLegacyText(
						"§4§l" + CommonConst.KICK_PREFIX + "\n§f\n§fO servidor direcionado não está disponível!"));
				return;
			}

			if (server.isFull()) {
				player.sendMessage("§c§l> §fO servidor que você foi redirecionado está §ccheio§f!");
				return;
			}

			player.sendMessage("§c§l> §fO servidor que você foi redirecionado não está mais disponível!");
		} else {
			server = manager.getServer(event.getTarget().getName());

			if (server == null || server.getServerInfo() == null) {
				server = manager.getBalancer(ServerType.LOBBY).next();

				if (server == null || server.getServerInfo() == null) {
					event.setCancelled(true);
					event.getPlayer().disconnect(TextComponent.fromLegacyText(
							"§4§l" + CommonConst.KICK_PREFIX + "\n§f\n§fO servidor direcionado não está disponível!"));
					return;
				}

				if (server.isFull()) {
					player.sendMessage("§c§l> §fO servidor que você foi redirecionado está §ccheio§f!");
					return;
				}

				player.sendMessage("§c§l> §fO servidor que você foi redirecionado não está mais disponível!");
			}
		}

		if (server.getServerType() == ServerType.SCREENSHARE) {
			event.setTarget(server.getServerInfo());
			return;
		}

		if (!player.getLoginConfiguration().isLogged()) {
			if (CommonGeneral.getInstance().isLoginServer()) {
				if (server.getServerType() != ServerType.LOGIN) {
					server = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.LOGIN).next();

					if (server == null || server.getServerInfo() == null) {
						event.setCancelled(true);
						event.getPlayer().disconnect(TextComponent.fromLegacyText("§4§l" + CommonConst.KICK_PREFIX
								+ "\n§f\n§fNenhum servidor de §alogin§f está disponível no momento!\n§f\n§6Acesse nosso discord para mais informações:\n§b"
								+ CommonConst.DISCORD));
						return;
					}

					if (server.isFull()) {
						event.setCancelled(true);
						event.getPlayer().disconnect(TextComponent.fromLegacyText("§4§l" + CommonConst.KICK_PREFIX
								+ "\n§f\n§fO servidor de §alogin§f está cheio no momento!\n§f\n§6Acesse nosso discord para mais informações:\n§b"
								+ CommonConst.DISCORD));
						return;
					}

					if (!player.getServerId().equals(server.getServerId())) {
						event.setTarget(server.getServerInfo());
					}

					player.sendMessage("§c§l> §fVocê não pode mudar de servidor!");
					return;
				}
			} else {
				if (server.getServerType() != ServerType.LOBBY) {
					server = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.LOBBY).next();

					if (server == null || server.getServerInfo() == null) {
						event.setCancelled(true);
						event.getPlayer().disconnect(TextComponent.fromLegacyText("§4§l" + CommonConst.KICK_PREFIX
								+ "\n§f\n§fNenhum servidor de §alobby§f está disponível no momento!\n§f\n§6Acesse nosso discord para mais informações:\n§b"
								+ CommonConst.DISCORD));
						return;
					}

					if (!player.getServerId().equals(server.getServerId())) {
						event.setTarget(server.getServerInfo());
					}

					player.sendMessage("§c§l> §fVocê não pode mudar de servidor!");
					return;
				}
			}
		}

		event.setTarget(server.getServerInfo());
	}

	@EventHandler
	public void onServerConnectRequest(PostLoginEvent event) {
		Member player = CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());

		if (player == null)
			return;

		AccountType accountType = player.getLoginConfiguration().getAccountType();
		BattleServer server = manager.getServer(getServerIp(event.getPlayer().getPendingConnection()));

		if (server == null) {
			server = manager.getBalancer(
					accountType == AccountType.CRACKED && CommonGeneral.getInstance().isLoginServer() ? ServerType.LOGIN
							: ServerType.LOBBY)
					.next();

			if (server == null || server.getServerInfo() == null) {
				event.getPlayer().disconnect(TextComponent.fromLegacyText(accountType == AccountType.CRACKED ? "§4§l"
						+ CommonConst.KICK_PREFIX
						+ "\n§f\n§fO servidor de §alogin§f está cheio no momento!\n§f\n§6Acesse nosso discord para mais informações:\n§b"
						+ CommonConst.DISCORD
						: "§4§l" + CommonConst.KICK_PREFIX
								+ "\n\n§fNenhum servidor de §alobby§f está disponível no momento!\n§f\n§6Acesse nosso discord para mais informações:\n§b"
								+ CommonConst.DISCORD));
				return;
			}
		}

		event.getPlayer().connect(server.getServerInfo());
	}

	@EventHandler(priority = 127)
	public void onProxyPing(ProxyPingEvent event) {
		String serverIp = getServerIp(event.getConnection());
		BattleServer server = manager.getServer(serverIp);
		ServerPing serverPing = event.getResponse();

		if (server == null) {
			serverPing.getPlayers().setMax(ProxyServer.getInstance().getOnlineCount() + 1);
			serverPing.getPlayers().setOnline(ProxyServer.getInstance().getOnlineCount());
			serverPing.getPlayers()
					.setSample(new PlayerInfo[] { new PlayerInfo("§e" + CommonConst.WEBSITE, UUID.randomUUID()) });
			serverPing.setDescription("      §f﹄ §6§lSaint§f§lMC §f| §eMinecraft Network §7(1.7-.12) §f﹃\n"
					+ StringCenter.centered(motdList[new Random().nextInt(motdList.length)], 127));
		} else {
			event.registerIntent(BungeeMain.getPlugin());

			server.getServerInfo().ping(new Callback<ServerPing>() {

				@Override
				public void done(ServerPing realPing, Throwable throwable) {
					if (throwable == null) {
						serverPing.getPlayers().setMax(realPing.getPlayers().getMax());
						serverPing.getPlayers().setOnline(realPing.getPlayers().getOnline());
						serverPing.setDescription(realPing.getDescription());
					} else {
						serverPing.setPlayers(new Players(-1, -1, null));
						serverPing.setVersion(new Protocol("-1", 0));
						serverPing.setDescription(
								"    §f﹄ §6§lSaint§f§lMC §f| §eMinecraft Network §7(1.7-.12) §f﹃\n    §4§nServidor não encontrado!");
					}

					event.setResponse(serverPing);
					event.completeIntent(BungeeMain.getPlugin());
				}

			});
		}
	}

	public static String getServerIp(PendingConnection con) {
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
