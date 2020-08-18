package tk.yallandev.saintmc.bukkit.event.server;

import java.util.UUID;

import lombok.Getter;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;

@Getter
public class ServerPlayerLeaveEvent extends ServerEvent {
	
	private UUID uniqueId;

	public ServerPlayerLeaveEvent(UUID uniqueId, String serverId, ServerType serverType, ProxiedServer proxiedServer) {
		super(serverId, serverType, proxiedServer);
		this.uniqueId = uniqueId;
	}

}