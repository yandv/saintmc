package tk.yallandev.saintmc.common.server.loadbalancer.server;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import tk.yallandev.saintmc.common.profile.Profile;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.element.LoadBalancerObject;
import tk.yallandev.saintmc.common.server.loadbalancer.element.NumberConnection;

@Getter
public class ProxiedServer implements LoadBalancerObject, NumberConnection {

	private String serverId;
	private ServerType serverType;
	
	private Set<UUID> players;
	private Set<Profile> profile;
	
	private int maxPlayers;
	
	private boolean joinEnabled;

	public ProxiedServer(String serverId, ServerType serverType, Set<UUID> players, Set<Profile> profile, int maxPlayers, boolean joinEnabled) {
		this.serverId = serverId.toLowerCase();
		this.serverType = serverType;
		this.players = players;
		this.profile = profile;
		this.maxPlayers = maxPlayers;
		this.joinEnabled = joinEnabled;
	}
	
	public void setOnlinePlayers(Set<UUID> onlinePlayers) {
		this.players = onlinePlayers;
	}

	public void joinPlayer(UUID uuid) {
		players.add(uuid);
	}

	public void leavePlayer(UUID uuid) {
		players.remove(uuid);
	}
	
	public void addWhitelist(Profile profile) {
		this.profile.add(profile);
	}
	
	public void removeWhitelist(Profile profile) {
		Iterator<Profile> iterator = this.profile.iterator();
		
		while (iterator.hasNext()) {
			Profile prof = iterator.next();
			
			if (prof.equals(profile.getUniqueId()) || prof.equals(profile.getPlayerName()))
				iterator.remove();
		}
	}
	
	public boolean isInWhitelist(String playerName) {
		return this.profile.stream().filter(profile -> profile.getPlayerName().equals(playerName)).findFirst().isPresent();
	}
	
	public boolean isInWhitelist(Profile profile) {
		return this.profile.contains(profile);
	}

	public int getOnlinePlayers() {
		return players.size();
	}

	public boolean isFull() {
		return players.size() >= maxPlayers;
	}

	public void setJoinEnabled(boolean joinEnabled) {
		this.joinEnabled = joinEnabled;
	}

	public ServerInfo getServerInfo() {
		return ProxyServer.getInstance().getServerInfo(serverId);
	}

	@Override
	public boolean canBeSelected() {
		return !isFull();
	}

	@Override
	public int getActualNumber() {
		return getOnlinePlayers();
	}

	@Override
	public int getMaxNumber() {
		return getMaxPlayers();
	}
}
