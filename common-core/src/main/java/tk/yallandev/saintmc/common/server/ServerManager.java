package tk.yallandev.saintmc.common.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import tk.yallandev.saintmc.common.server.loadbalancer.BaseBalancer;
import tk.yallandev.saintmc.common.server.loadbalancer.server.HungerGamesServer;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameServer;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;
import tk.yallandev.saintmc.common.server.loadbalancer.server.SkywarsServer;
import tk.yallandev.saintmc.common.server.loadbalancer.type.LeastConnection;
import tk.yallandev.saintmc.common.server.loadbalancer.type.MostConnection;

/**
 * 
 * ServerManager to control and loadbalance all connected servers
 * 
 * @author yandv
 * @since 1.0
 *
 */

public class ServerManager {

	private Map<String, ProxiedServer> activeServers;
	private Map<ServerType, BaseBalancer<ProxiedServer>> balancers;

	public ServerManager() {
		balancers = new HashMap<>();
		activeServers = new HashMap<>();

		balancers.put(ServerType.LOBBY, new LeastConnection<>());
		balancers.put(ServerType.LOGIN, new LeastConnection<>());
		balancers.put(ServerType.SCREENSHARE, new LeastConnection<>());

		balancers.put(ServerType.FULLIRON, new MostConnection<>());
		balancers.put(ServerType.SIMULATOR, new MostConnection<>());
		balancers.put(ServerType.GLADIATOR, new MostConnection<>());

		balancers.put(ServerType.HUNGERGAMES, new MostConnection<>());
		balancers.put(ServerType.PRIVATE_SERVER, new MostConnection<>());
		balancers.put(ServerType.CLANXCLAN, new MostConnection<>());
		balancers.put(ServerType.EVENTO, new MostConnection<>());

		balancers.put(ServerType.SW_SOLO, new MostConnection<>());
		balancers.put(ServerType.SW_TEAM, new MostConnection<>());
		balancers.put(ServerType.SW_SQUAD, new MostConnection<>());
	}

	public BaseBalancer<ProxiedServer> getBalancer(ServerType type) {
		return balancers.get(type);
	}

	public void putBalancer(ServerType type, BaseBalancer<ProxiedServer> balancer) {
		balancers.put(type, balancer);
	}

	public ProxiedServer addActiveServer(String serverAddress, String serverIp, ServerType type, int maxPlayers) {
		return updateActiveServer(serverIp, type, new HashSet<>(), maxPlayers, true);
	}

	public ProxiedServer updateActiveServer(String serverId, ServerType type, Set<UUID> onlinePlayers, int maxPlayers,
			boolean canJoin) {
		return updateActiveServer(serverId, type, onlinePlayers, maxPlayers, canJoin, 0, "Unknown", null);
	}

	public ProxiedServer updateActiveServer(String serverId, ServerType type, Set<UUID> onlinePlayers, int maxPlayers,
			boolean canJoin, int tempo, String map, MinigameState state) {
		ProxiedServer server = activeServers.get(serverId);

		if (server == null) {
			if (type == ServerType.HUNGERGAMES)
				server = new HungerGamesServer(serverId, type, onlinePlayers, new HashSet<>(), true);
			else if (type.name().startsWith("SW"))
				server = new SkywarsServer(serverId, type, onlinePlayers, new HashSet<>(), true);
			else
				server = new ProxiedServer(serverId, type, onlinePlayers, new HashSet<>(), maxPlayers, true);

			activeServers.put(serverId.toLowerCase(), server);
		}

		server.setOnlinePlayers(onlinePlayers);
		server.setJoinEnabled(canJoin);

		if (state != null && server instanceof MinigameServer) {
			((MinigameServer) server).setState(state);
			((MinigameServer) server).setTime(tempo);
			((MinigameServer) server).setMap(map);
		}

		addToBalancers(serverId, server);
		return server;
	}

	public ProxiedServer getServer(String serverName) {
		return activeServers.get(serverName.toLowerCase());
	}

	public ProxiedServer getServerByName(String serverName) {
		for (ProxiedServer proxiedServer : activeServers.values()) {
			if (proxiedServer.getServerId().toLowerCase().startsWith(serverName.toLowerCase()))
				return proxiedServer;
		}

		return activeServers.get(serverName.toLowerCase());
	}

	public Collection<ProxiedServer> getServers() {
		return activeServers.values();
	}

	public void removeActiveServer(String str) {
		if (getServer(str) != null)
			removeFromBalancers(getServer(str));

		activeServers.remove(str.toLowerCase());
	}

	public void addToBalancers(String serverId, ProxiedServer server) {
		BaseBalancer<ProxiedServer> balancer = getBalancer(server.getServerType());

		if (balancer == null)
			return;

		balancer.add(serverId.toLowerCase(), server);
	}

	public void removeFromBalancers(ProxiedServer serverId) {
		BaseBalancer<ProxiedServer> balancer = getBalancer(serverId.getServerType());
		if (balancer != null)
			balancer.remove(serverId.getServerId().toLowerCase());
	}

	public int getTotalNumber() {
		int totalNumber = 0;

		for (ServerType serverType : ServerType.values()) {
			if (getBalancer(serverType) != null) {
				totalNumber += getBalancer(serverType).getTotalNumber();
			}
		}

		return totalNumber;
	}

}