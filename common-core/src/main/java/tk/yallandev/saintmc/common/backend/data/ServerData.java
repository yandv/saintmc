package tk.yallandev.saintmc.common.backend.data;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;

public interface ServerData {
	
	/*
	 * Get Server Info
	 */
	
	String getServerId(String ipAddress);
	
	ServerType getServerType(String ipAddress);
	
	/*
	 * Server Network Info
	 */
	
	Map<String, Map<String, String>> loadServers();
	
	Set<UUID> getPlayers(String serverId);
	
	/*
	 * Network Server Manager
	 */
	
	void startServer(int maxPlayers);
	
	void updateStatus(MinigameState state, int time);
	
	void updateStatus(MinigameState state, String map, int time);
	
	void setJoinEnabled(boolean bol);
	
	void stopServer();
	
	/*
	 * Player Server Manager
	 */
	
	void joinPlayer(UUID uuid);
	
	void leavePlayer(UUID uuid);
	
	/*
	 * Player Server Info
	 */
	
	long getPlayerCount(String serverId);
	
	long getPlayerCount(ServerType serverType);
	
	/*
	 * Connection
	 */
	
	void closeConnection();
	
}