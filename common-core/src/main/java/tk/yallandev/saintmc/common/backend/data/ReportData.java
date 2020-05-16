package tk.yallandev.saintmc.common.backend.data;

import java.util.Collection;
import java.util.UUID;

import tk.yallandev.saintmc.common.report.Report;
import tk.yallandev.saintmc.common.utils.supertype.Callback;

public interface ReportData {
	
	Collection<Report> loadReports();
	
	Report loadReport(UUID uniqueId);

	void saveReport(Report report);
	
	void saveReport(Report report, Callback<Report> callback);
	
	void deleteReport(UUID uniqueId);
	
	void updateReport(Report report, String fieldName);
	
	void updateName(UUID uniqueId, String playerName);


}
