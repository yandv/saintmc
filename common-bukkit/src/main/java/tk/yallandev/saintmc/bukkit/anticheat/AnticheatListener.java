package tk.yallandev.saintmc.bukkit.anticheat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import tk.yallandev.saintmc.bukkit.event.report.ReportReceiveEvent;
import tk.yallandev.saintmc.common.report.Report;


/**
 * Smart report
 * 
 * @author yandv
 *
 */

public class AnticheatListener implements Listener {
	
	@EventHandler
	public void onReportReceive(ReportReceiveEvent event) {
		Report report = event.getReport();
		Player player = Bukkit.getPlayer(report.getPlayerUniqueId());
		
		if (player == null)
			return;
		
		player.sendMessage("Start SmartReport");
	}
	
}
