package tk.yallandev.anticheat.check;

import java.util.function.Predicate;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;

@Getter
public class Hack implements Listener {
	
	private String name;

	@Setter
	private boolean autoBan;
	@Setter
	private int maxAlerts = 30;
	@Setter
	private boolean alertBungee;
	
	public Hack() {
		name = getClass().getSimpleName().replace("Hack", "");
//		alertMap = new HashMap<>();
	}
	
	public void alert(Player player) {
		alert(player, 0);
	}

	public void alert(Player player, int cps) {
//		int alerts = alertMap.put(player, alertMap.computeIfAbsent(player, v -> 0) + 1) + 1;
//
//		if (alerts > this.maxAlerts)
//			return;
//
		broadcast(
				member -> member.hasGroupPermission(Group.TRIAL)
						&& member.getAccountConfiguration().isAnticheatEnabled(),
				"§9Anticheat> §fO jogador §d" + player.getName() + "§f está usando §c" + getName()
						+ (cps > 0 ? " §4(" + cps + " cps)" : "") + " §7(" + 1 + "/" + maxAlerts + ")!");
//
//		if (alertBungee)
//			if (alerts >= 10 && alerts % 5 == 0)
//				BukkitMain.getInstance().getPacketController()
//						.sendPacket(new AnticheatAlertPacket(getName(), cps, alerts, maxAlerts), player);
//
//		if (alerts == this.maxAlerts) {
//			controller.autoban(player, name);
//			broadcast(member -> member.hasGroupPermission(Group.TRIAL), "§9Anticheat> §fO jogador §d" + player.getName()
//					+ "§f será auto banido por §c" + getName() + "§f em §a1 minuto§f!");
//		}
	}
	
	public void autoban(Player player, long time) {
//		controller.autoban(player, name, time);
	}

	public void broadcast(Predicate<? super Member> filter, String message) {
		CommonGeneral.getInstance().getMemberManager().getMembers().stream().filter(filter)
				.forEach(member -> member.sendMessage(message));
	}

}
