package tk.yallandev.saintmc.bungee.listener;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.bungee.bungee.BungeeMember;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;

public class ScreenshareListener implements Listener {

	@EventHandler
	public void onServerSwitch(ServerSwitchEvent event) {
		if (event.getFrom() == null)
			return;

		BungeeMember member = (BungeeMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());
		ProxiedServer server = BungeeMain.getInstance().getServerManager().getServer(event.getFrom().getName());

		if (server == null)
			return;

		if (server.getServerType() == ServerType.SCREENSHARE) {
			if (member.isScreensharing()) {
				member.setScreensharing(false);
				member.setScreenshareStaff(null);

				server.getPlayers().stream().map(uuid -> ProxyServer.getInstance().getPlayer(uuid))
						.forEach(player -> player
								.sendMessage("§cO jogador " + member.getPlayerName() + " saiu da Screenshare!"));
			}
		}
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent event) {
		ProxiedPlayer player = event.getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		if (member == null)
			return;

		if (player.getServer() == null)
			return;

		ProxiedServer server = BungeeMain.getInstance().getServerManager()
				.getServer(player.getServer().getInfo().getName());

		if (server == null)
			return;

		if (server.getServerType() == ServerType.SCREENSHARE && member.hasGroupPermission(Group.MODGC)) {
			if (member.hasGroupPermission(Group.MODGC)) {

				UUID uuid = member.getUniqueId();

				server.getPlayers().stream()
						.map(u -> (BungeeMember) CommonGeneral.getInstance().getMemberManager().getMember(u))
						.filter(m -> m.getScreenshareStaff().equals(member.getUniqueId())).forEach(m -> {
							m.sendMessage("§cO " + member.getPlayerName()
									+ " foi desconectado do servidor, caso ele não volte em 90 segundos você será liberado!");

							ProxyServer.getInstance().getScheduler().schedule(BungeeMain.getInstance(), () -> {
								ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);

								if (p == null) {
									m.sendMessage("§aVocê foi liberado, digite /lobby para sair do servidor!");
									m.setScreensharing(false);
									m.setScreenshareStaff(null);
								}
							}, 90, TimeUnit.SECONDS);
						});
			}
		}
	}

}
