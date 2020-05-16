package tk.yallandev.saintmc.bungee.manager;

import java.net.InetSocketAddress;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import tk.yallandev.saintmc.common.server.ServerManager;
import tk.yallandev.saintmc.common.server.ServerType;

public class BungeeServerManager extends ServerManager {

	@Override
	public void addActiveServer(String serverAddress, String serverIp, ServerType type, int maxPlayers) {
		super.addActiveServer(serverAddress, serverIp, type, maxPlayers);

		if (!ProxyServer.getInstance().getServers().containsKey(serverIp.toLowerCase())) {
			String ipAddress = serverAddress.split(":")[0];
			int port = Integer.valueOf(serverAddress.split(":")[1]);

			ServerInfo localServerInfo = ProxyServer.getInstance().constructServerInfo(serverIp.toLowerCase(),
					new InetSocketAddress(ipAddress, port), "Restarting", false);

			ProxyServer.getInstance().getServers().put(serverIp.toLowerCase(), localServerInfo);
		}
	}

	@Override
	public void removeActiveServer(String str) {
		super.removeActiveServer(str);

		if (ProxyServer.getInstance().getServers().containsKey(str.toLowerCase())) {
			ProxyServer.getInstance().getServers().remove(str.toLowerCase());
		}
	}

}
