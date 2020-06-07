package tk.yallandev.saintmc.bungee.packet;

import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.bungee.account.BungeeMember;
import tk.yallandev.saintmc.common.ban.constructor.Ban;
import tk.yallandev.saintmc.common.networking.Packet;
import tk.yallandev.saintmc.common.networking.packet.AnticheatAlertPacket;
import tk.yallandev.saintmc.common.networking.packet.AnticheatBanPacket;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;

public class BungeePacketHandler implements tk.yallandev.saintmc.common.networking.PacketHandler {

	@Override
	public void handlePacket(Packet packet, ProxiedServer server, ProxiedPlayer player) {
		BungeeMember member = (BungeeMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(player.getUniqueId());

		if (packet instanceof AnticheatBanPacket) {
			AnticheatBanPacket anticheatPacket = (AnticheatBanPacket) packet;

			BungeeMain.getInstance().getPunishManager().ban(member, new Ban(member.getUniqueId(), "CONSOLE",
					UUID.randomUUID(), "Autoban - " + anticheatPacket.getHackType(), anticheatPacket.getBanTime()));
		} else if (packet instanceof AnticheatAlertPacket) {
			AnticheatAlertPacket anticheatPacket = (AnticheatAlertPacket) packet;

			int cps = anticheatPacket.getCps();
			String hackName = anticheatPacket.getHackType();
			int alerts = anticheatPacket.getAlerts();
			int maxAlerts = anticheatPacket.getMaxAlerts();
			
			for (ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
				if (online.getServer().getInfo().getName().equalsIgnoreCase(server.getServerId()))
					continue;
				
				online.sendMessage("§9Anticheat> §fO jogador §d" + player.getName() + "§f está usando §c" + hackName
						+ (cps > 0 ? " §4(" + cps + " cps)" : "") + " §7(" + alerts + "/" + maxAlerts + ")!");
			}
		}

//		handleAnticheat();
	}

}
