package tk.yallandev.saintmc.bukkit.anticheat.modules;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.anticheat.AnticheatController;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.networking.packet.AnticheatAlertPacket;
import tk.yallandev.saintmc.common.permission.Group;

@AllArgsConstructor
@Getter
public abstract class Module implements Listener {

	private String name;

	@Setter
	private boolean autoBan;
	@Setter
	private int maxAlerts = 30;
	@Setter
	private boolean alertBungee;

	private Map<Player, Integer> alertMap;
	private AnticheatController controller;

	public Module() {
		name = getClass().getSimpleName().replace("Module", "");
		alertMap = new HashMap<>();
		controller = BukkitMain.getInstance().getAnticheatController();
	}

	public void alert(Player player) {
		alert(player, 0);
	}

	public void alert(Player player, int cps) {
		int alerts = alertMap.put(player, alertMap.computeIfAbsent(player, v -> 0) + 1) + 1;

		if (alerts > this.maxAlerts)
			return;

		broadcast(
				member -> member.hasGroupPermission(Group.TRIAL)
						&& member.getAccountConfiguration().isAnticheatEnabled(),
				"§9Anticheat> §fO jogador §d" + player.getName() + "§f está usando §c" + getName()
						+ (cps > 0 ? " §4(" + cps + " cps)" : "") + " §7(" + alerts + "/" + maxAlerts + ")!");

		if (alertBungee)
			if (alerts >= 10 && alerts % 5 == 0)
				BukkitMain.getInstance().getPacketController()
						.sendPacket(new AnticheatAlertPacket(getName(), cps, alerts, maxAlerts), player);

		if (alerts == this.maxAlerts) {
			controller.autoban(player, name);
			broadcast(member -> member.hasGroupPermission(Group.TRIAL), "§9Anticheat> §fO jogador §d" + player.getName()
					+ "§f será auto banido por §c" + getName() + "§f em §a1 minuto§f!");
		}
	}
	
	public void autoban(Player player, long time) {
		controller.autoban(player, name, time);
	}

	public void broadcast(Predicate<? super Member> filter, String message) {
		CommonGeneral.getInstance().getMemberManager().getMembers().stream().filter(filter)
				.forEach(member -> member.sendMessage(message));
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if (alertMap.containsKey(player))
			alertMap.remove(player);
	}

}
