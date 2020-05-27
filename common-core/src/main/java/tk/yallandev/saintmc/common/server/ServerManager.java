package tk.yallandev.saintmc.common.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import tk.yallandev.saintmc.common.server.loadbalancer.BaseBalancer;
import tk.yallandev.saintmc.common.server.loadbalancer.server.BattleServer;
import tk.yallandev.saintmc.common.server.loadbalancer.server.HungerGamesServer;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameServer;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.common.server.loadbalancer.type.LeastConnection;
import tk.yallandev.saintmc.common.server.loadbalancer.type.MostConnection;

public class ServerManager {

    private Map<String, BattleServer> activeServers;

    private HashMap<ServerType, BaseBalancer<BattleServer>> balancers;

    public ServerManager() {
        balancers = new HashMap<>();

        balancers.put(ServerType.LOBBY, new LeastConnection<>());
        balancers.put(ServerType.LOGIN, new LeastConnection<>());
        balancers.put(ServerType.SCREENSHARE, new LeastConnection<>());
        
        balancers.put(ServerType.FULLIRON, new MostConnection<>());
        balancers.put(ServerType.SIMULATOR, new MostConnection<>());
        balancers.put(ServerType.GLADIATOR, new MostConnection<>());

        balancers.put(ServerType.HUNGERGAMES, new MostConnection<>());

        activeServers = new HashMap<>();
    }

    public BaseBalancer<BattleServer> getBalancer(ServerType type) {
        return balancers.get(type);
    }

    public void putBalancer(ServerType type, BaseBalancer<BattleServer> balancer) {
        balancers.put(type, balancer);
    }

    public void addActiveServer(String serverAddress, String serverIp, ServerType type, int maxPlayers) {
        updateActiveServer(serverIp, type, new HashSet<>(), maxPlayers, true);
    }

    public void updateActiveServer(String serverId, ServerType type, Set<UUID> onlinePlayers, int maxPlayers, boolean canJoin) {
        updateActiveServer(serverId, type, onlinePlayers, maxPlayers, canJoin, 0, "Unknown", null);
    }

    public void updateActiveServer(String serverId, ServerType type, Set<UUID> onlinePlayers, int maxPlayers, boolean canJoin, int tempo, String map, MinigameState state) {
        BattleServer server = activeServers.get(serverId);
        if (server == null) {
            if (type == ServerType.HUNGERGAMES) {
                server = new HungerGamesServer(serverId, type, onlinePlayers, true);
            } else {
                server = new BattleServer(serverId, type, onlinePlayers, maxPlayers, true);
            }
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
    }

    public BattleServer getServer(String str) {
        return activeServers.get(str.toLowerCase());
    }
    
    public Collection<BattleServer> getServers() {
        return activeServers.values();
    }

    public void removeActiveServer(String str) {
        if (getServer(str) != null)
            removeFromBalancers(getServer(str));
        
        activeServers.remove(str.toLowerCase());
    }

    public void addToBalancers(String serverId, BattleServer server) {
        BaseBalancer<BattleServer> balancer = getBalancer(server.getServerType());
        
        if (balancer == null)
            return;
        
        balancer.add(serverId.toLowerCase(), server);
    }

    public void removeFromBalancers(BattleServer serverId) {
        BaseBalancer<BattleServer> balancer = getBalancer(serverId.getServerType());
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
