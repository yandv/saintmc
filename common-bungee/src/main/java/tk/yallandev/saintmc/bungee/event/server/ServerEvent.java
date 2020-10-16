package tk.yallandev.saintmc.bungee.event.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;

@Getter
@AllArgsConstructor
public class ServerEvent extends Event {

	private ProxiedServer proxiedServer;

}
