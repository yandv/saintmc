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

public class AlertController implements net.saintmc.anticheat.alert.AlertController {

	public AlertController() {
		Bukkit.getPluginManager().registerEvents(new Listener() {

			@EventHandler
			public void onUpdate(UpdateEvent event) {
				if (event.getType() == UpdateType.SECOND)
					for (Member member : MemberController.INSTANCE.getCollection()) {
						member.alert();
					}
			}

		}, BukkitMain.getInstance());
	}

	@Override
	public void alert(Player player, Alert alert) {
		MessageBuilder messageBuilder = new MessageBuilder("§9Anticheat> §fO jogador §d" + alert.getPlayerName()
				+ "§f está usando §c" + NameUtils.formatString(alert.getAlertType().name()) + "§f!");

		if (alert.hasMetadata())
			messageBuilder.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					TextComponent.fromLegacyText(Joiner.on("\n").join(alert.getMetadataList().stream()
							.map(e -> "§c" + e.getKey() + ": §f" + e.getObject()).collect(Collectors.toList())))));

		CommonGeneral.getInstance().getMemberManager().getMemberMap().values().stream()
				.filter(member -> member.hasGroupPermission(Group.TRIAL)
						&& member.getAccountConfiguration().isAnticheatEnabled())
				.forEach(member -> member.sendMessage(messageBuilder.create()));

		BukkitMain.getInstance().getPacketController().sendPacket(
				new AnticheatAlertPacket(NameUtils.formatString(alert.getAlertType().name()), new JsonObject()),
				player);
	}

	@Override
	public void autoban(Player player, long time) {
		BukkitMain.getInstance().getPacketController().sendPacket(
				new AnticheatBanPacket("Uso de Hack", System.currentTimeMillis() + (24 * 60 * 60 * 1000 * 14)), player);
	}

}
