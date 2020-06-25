package tk.yallandev.saintmc.bukkit.listener.register;

import java.util.stream.Collectors;

import org.bukkit.Sound;
import org.bukkit.event.EventHandler;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.account.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.report.ReportReceiveEvent;
import tk.yallandev.saintmc.common.permission.Group;

public class ReportListener {

	@EventHandler
	public void onReportReceive(ReportReceiveEvent event) {
		CommonGeneral.getInstance().getMemberManager().getMembers().stream().filter(
				player -> player.getAccountConfiguration().isReportEnabled() && player.hasGroupPermission(Group.HELPER))
				.collect(Collectors.toList()).forEach(member -> ((BukkitMember) member).getPlayer()
						.playSound(((BukkitMember) member).getPlayer().getLocation(), Sound.LEVEL_UP, 0.1f, 0.1f));
	}

}