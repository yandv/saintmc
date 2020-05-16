package tk.yallandev.saintmc.common.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.report.Report;

public class ReportManager {
	
	private Map<UUID, Report> reports;

	public ReportManager() {
		reports = new HashMap<>();
	}

	public void loadReport(Report report) {
		reports.put(report.getUniqueId(), report);
	}

	public void unloadReport(UUID uniqueId) {
		if (reports.containsKey(uniqueId))
			reports.remove(uniqueId);
		else
			CommonGeneral.getInstance().getLogger().log(Level.SEVERE, "NAO FOI POSSIVEL ENCONTRAR Report " + uniqueId.toString());
	}

	public Report getReport(UUID uniqueId) {
		return reports.get(uniqueId);
	}

	public Report getReport(String reportName) {
		for (Report Report : reports.values()) {
			if (Report.getPlayerName().equalsIgnoreCase(reportName))
				return Report;
		}
		
		return null;
	}

	public Collection<Report> getReports() {
		return reports.values();
	}

}
