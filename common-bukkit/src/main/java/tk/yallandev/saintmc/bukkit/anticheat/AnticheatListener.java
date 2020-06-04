package tk.yallandev.saintmc.bukkit.anticheat;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.anticheat.AnticheatController.Autoban;
import tk.yallandev.saintmc.bukkit.event.report.ReportReceiveEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
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
	
	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND) {
			
			Iterator<Entry<UUID, Autoban>> iterator = BukkitMain.getInstance().getAnticheatController().getBanMap().entrySet().iterator();
			
			while (iterator.hasNext()) {
				Entry<UUID, Autoban> entry = iterator.next();
				
				long time = entry.getValue().getExpireTime() - System.currentTimeMillis();
				
				if (time <= 0) {
					
					Bukkit.broadcastMessage("§cUm jogador de sua sala foi banido usando trapaças!");
					iterator.remove();
					continue;
				}
				
				Bukkit.broadcastMessage("§c" + (time / 1000) + " segundos para ser banido!");
			}
			
		}
	}

}
