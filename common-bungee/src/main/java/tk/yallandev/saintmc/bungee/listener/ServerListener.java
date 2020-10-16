package tk.yallandev.saintmc.bungee.listener;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.event.server.ServerUpdateEvent;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.common.utils.string.MessageBuilder;
import tk.yallandev.saintmc.common.utils.string.StringUtils;
import tk.yallandev.saintmc.common.utils.string.StringUtils.TimeFormat;

public class ServerListener implements Listener {

	@EventHandler
	public void onServerUpdate(ServerUpdateEvent event) {
		if (event.getProxiedServer().getServerType() == ServerType.HUNGERGAMES) {
			if ((event.getLastState() == MinigameState.WAITING && event.getState() == MinigameState.STARTING
					&& event.getTime() >= 180) || event.getState() == MinigameState.PREGAME) {
				int time = event.getTime();

				if (time == 60 || time == 120) {
					String[] split = event.getProxiedServer().getServerId().split("\\.");
					String serverId = split.length > 0 ? split[0] : event.getProxiedServer().getServerId();

					String message = "§6§lPARTIDA: §eO §6HG-" + serverId.toUpperCase() + "§e irá iniciar em §6"
							+ StringUtils.formatTime(time, TimeFormat.SHORT) + " §ecom mais de §b"
							+ event.getProxiedServer().getOnlinePlayers() + " jogadores! §6Clique aqui§e para jogar!";

					CommonGeneral.getInstance().getMemberManager().broadcast(
							new MessageBuilder(message).setClickEvent(ClickEvent.Action.RUN_COMMAND,
									"/connect " + event.getProxiedServer().getServerId()).create(),
							member -> member.getServerType() != ServerType.HUNGERGAMES
									&& member.getServerType() != ServerType.EVENTO);
				}
			}
		}
	}

}
