package tk.yallandev.saintmc.bungee.networking.packet;

import java.util.UUID;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.bungee.bungee.BungeeMember;
import tk.yallandev.saintmc.common.ban.constructor.Ban;
import tk.yallandev.saintmc.common.networking.Packet;
import tk.yallandev.saintmc.common.networking.packet.AnticheatAlertPacket;
import tk.yallandev.saintmc.common.networking.packet.AnticheatBanPacket;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;
import tk.yallandev.saintmc.common.utils.string.MessageBuilder;

public class BungeePacketHandler implements tk.yallandev.saintmc.common.networking.PacketHandler {

	@Override
	public void handlePacket(Packet packet, ProxiedServer server, ProxiedPlayer player) {
		BungeeMember member = (BungeeMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(player.getUniqueId());

		if (packet instanceof AnticheatBanPacket) {
			AnticheatBanPacket anticheatPacket = (AnticheatBanPacket) packet;

			BungeeMain.getInstance().getPunishManager().ban(member,
					new Ban(player.getUniqueId(), member.getPlayerName(), "Spectrum AC", UUID.randomUUID(),
							"Autoban - " + anticheatPacket.getHackType(), anticheatPacket.getBanTime() == -1 ? -1
									: anticheatPacket.getBanTime() - System.currentTimeMillis()));
		} else if (packet instanceof AnticheatAlertPacket) {
			AnticheatAlertPacket anticheatPacket = (AnticheatAlertPacket) packet;
			String hackName = anticheatPacket.getHackType();

			CommonGeneral.getInstance().getMemberManager().getMembers().stream()
					.filter(o -> !o.getServerId().equalsIgnoreCase(server.getServerId())
							&& o.hasGroupPermission(Group.TRIAL) && o.getAccountConfiguration().isAnticheatEnabled())
					.forEach(m -> m.sendMessage(new MessageBuilder(
							"§9Spectrum> §fO jogador §d" + player.getName() + "§f está usando §c" + hackName + " §e("
									+ anticheatPacket.getAlertIndex() + "/" + anticheatPacket.getMaxAlerts() + ")§f!")
											.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
													TextComponent
															.fromLegacyText("§7Servidor: §f" + member.getServerId())))
											.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
													"/tp " + member.getPlayerName()))
											.create()));
		}
	}

}
