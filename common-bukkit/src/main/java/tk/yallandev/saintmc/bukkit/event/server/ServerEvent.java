package tk.yallandev.saintmc.bukkit.event.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.NormalEvent;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;

@AllArgsConstructor
@Getter
public class ServerEvent extends NormalEvent {
	
	private String serverId;
	private ServerType serverType;
	
	private ProxiedServer proxiedServer;

}
