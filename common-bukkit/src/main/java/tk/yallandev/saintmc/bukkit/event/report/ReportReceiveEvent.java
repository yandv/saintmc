package tk.yallandev.saintmc.bukkit.event.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.NormalEvent;
import tk.yallandev.saintmc.common.report.Report;

@Getter
@AllArgsConstructor
public class ReportReceiveEvent extends NormalEvent {
	
	private Report report;
	
}
