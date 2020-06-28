package tk.yallandev.saintmc.common.controller;

import java.util.Collection;
import java.util.UUID;
import java.util.logging.Level;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.report.Report;

public class ReportManager extends StoreController<UUID, Report> {
	
	public void loadReport(Report report) {
		load(report.getUniqueId(), report);
	}

	public void unloadReport(UUID uniqueId) {
		if (!unload(uniqueId))
			CommonGeneral.getInstance().getLogger().log(Level.SEVERE, "NAO FOI POSSIVEL ENCONTRAR Report " + uniqueId.toString());
	}

	public Report getReport(UUID uniqueId) {
		return getValue(uniqueId);
	}

	public Report getReport(String reportName) {
		for (Report Report : getStoreMap().values()) {
			if (Report.getPlayerName().equalsIgnoreCase(reportName))
				return Report;
		}
		
		return null;
	}

	public Collection<Report> getReports() {
		return getStoreMap().values();
	}

}
