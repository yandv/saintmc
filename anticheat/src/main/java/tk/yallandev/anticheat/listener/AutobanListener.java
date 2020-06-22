package tk.yallandev.anticheat.listener;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import tk.yallandev.anticheat.AnticheatController;
import tk.yallandev.anticheat.AnticheatController.Autoban;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.listener.ManualRegisterableListener;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.common.permission.Group;

public class AutobanListener extends ManualRegisterableListener {

	private AnticheatController controller;

	public AutobanListener(AnticheatController controller) {
		this.controller = controller;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if (controller.getBanMap().containsKey(player.getUniqueId())) {
			controller.getBanMap().get(player.getUniqueId()).ban();
			controller.getBanMap().remove(player.getUniqueId());
		}
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND) {
			Iterator<Entry<UUID, Autoban>> iterator = controller.getBanMap().entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<UUID, Autoban> entry = iterator.next();

				if (entry.getValue().getExpireTime() < System.currentTimeMillis()) {
					entry.getValue().ban();
					iterator.remove();
					continue;
				}

				int time = (int) ((entry.getValue().getExpireTime() - System.currentTimeMillis()) / 1000);

				if (time == 30 || time == 15 || time == 5) {
					CommonGeneral.getInstance().getMemberManager().getMembers().stream()
							.filter(member -> member.hasGroupPermission(Group.TRIAL)
									&& member.getAccountConfiguration().isAnticheatEnabled())
							.forEach(member -> member.sendMessage("§cO jogador " + entry.getValue().getPlayerName()
									+ " será banido em " + time + " segundos!"));
				}
			}

			if (controller.getBanMap().isEmpty())
				unregisterListener();
		}
	}

}