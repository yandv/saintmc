package tk.yallandev.saintmc.bukkit.anticheat.alert;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.google.common.base.Joiner;
import com.google.gson.JsonObject;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.saintmc.anticheat.account.Member;
import net.saintmc.anticheat.alert.Alert;
import net.saintmc.anticheat.controller.MemberController;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.common.networking.packet.AnticheatAlertPacket;
import tk.yallandev.saintmc.common.networking.packet.AnticheatBanPacket;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.utils.string.MessageBuilder;
import tk.yallandev.saintmc.common.utils.string.NameUtils;
import tk.yallandev.saintmc.common.utils.string.StringUtils;

public class AlertController implements net.saintmc.anticheat.alert.AlertController {

	public AlertController() {
		Bukkit.getPluginManager().registerEvents(new Listener() {

			@EventHandler
			public void onUpdate(UpdateEvent event) {
				if (event.getType() == UpdateType.SECOND)
					for (Member member : MemberController.INSTANCE.getCollection()) {
						if (member.isBan()) {
							if (member.getBanTime() < System.currentTimeMillis())
								autoban(member.getPlayer(), member.getBanAlert(),
										System.currentTimeMillis() + member.getBanAlert().getAlertType().getBanTime());
							else if (((member.getBanTime() - System.currentTimeMillis()) / 1000) % 10 == 0)
								CommonGeneral.getInstance().getMemberManager().getMemberMap().values().stream()
										.filter(m -> m.hasGroupPermission(Group.MOD)
												&& m.getAccountConfiguration().isAnticheatEnabled())
										.forEach(m -> m.sendMessage("§9Anticheat> §fO jogador §c"
												+ member.getPlayerName() + "§f será auto banido em §4"
												+ (StringUtils.formatTime(
														(int) ((member.getBanTime() - System.currentTimeMillis())
																/ 1000)))
												+ "§f!"));
						} else
							member.alert();
					}
			}

		}, BukkitMain.getInstance());
	}

	@Override
	public void alert(Player player, Alert alert, int alertIndex) {
		MessageBuilder messageBuilder = new MessageBuilder("§9Anticheat> §fO jogador §d" + alert.getPlayerName()
				+ "§f está usando §c" + NameUtils.formatString(alert.getAlertType().name()) + " §e(" + alertIndex + "/"
				+ alert.getAlertType().getMaxAlerts() + ")§f!");

		if (alert.hasMetadata())
			messageBuilder.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					TextComponent.fromLegacyText(Joiner.on("\n")
							.join(alert.getMetadataList().stream()
									.map(e -> "§c" + NameUtils.formatString(e.getKey()) + ": §f" + e.getObject())
									.collect(Collectors.toList())))));

		CommonGeneral.getInstance().getMemberManager().getMemberMap().values().stream()
				.filter(member -> member.hasGroupPermission(Group.TRIAL)
						&& member.getAccountConfiguration().isAnticheatEnabled())
				.forEach(member -> member.sendMessage(messageBuilder.create()));

		if (alertIndex % 3 == 0)
			BukkitMain.getInstance().getPacketController()
					.sendPacket(new AnticheatAlertPacket(NameUtils.formatString(alert.getAlertType().name()),
							alertIndex, alert.getAlertType().getMaxAlerts(), new JsonObject()), player);
	}

	@Override
	public void autoban(Player player, Alert alert, long time) {
		BukkitMain.getInstance().getPacketController()
				.sendPacket(new AnticheatBanPacket(NameUtils.formatString(alert.getAlertType().name()),
						time <= 1000 ? -1 : System.currentTimeMillis() + (time)), player);
	}

}
