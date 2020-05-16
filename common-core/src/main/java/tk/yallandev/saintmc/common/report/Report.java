package tk.yallandev.saintmc.common.report;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import lombok.Getter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;

@Getter
public class Report {

	private String playerName;
	private UUID uniqueId;
	
	private HashMap<UUID, ReportInformation> playersReason;
	private int reportLevel;
	private long reportExpire = Long.MIN_VALUE;
	
	private UUID lastReport = null;
	
	@Getter
	private boolean online = false;

	public Report(UUID uniqueId, String playerName) {
		playersReason = new HashMap<>();
		reportLevel = 0;
		
		this.playerName = playerName;
		this.uniqueId = uniqueId;
		this.online = true;
	}

	public UUID getPlayerUniqueId() {
		return uniqueId;
	}

	public int getReportLevel() {
		return reportLevel;
	}
	
	public long getLastReportTime() {
		if (lastReport != null) {
			return getPlayersReason().get(lastReport).getReportTime();
		}
		
		return Long.MIN_VALUE;
	}
	
	public ReportInformation getLastReport() {
		if (lastReport != null) {
			return getPlayersReason().get(lastReport);
		}
		
		return null;
	}
	
	public boolean isExpired() {
		return reportExpire < getLastReportTime();
	}

	public void setReportLevel(int reportLevel) {
		this.reportLevel = reportLevel / playersReason.size();
		CommonGeneral.getInstance().getReportData().updateReport(this, "reportLevel");
	}
	
	public void setOnline(boolean online) {
		this.online = online;
		CommonGeneral.getInstance().getReportData().updateReport(this, "online");
	}
	
	public void setPlayerName(String playerName) {
		if (this.playerName != playerName) {
			this.playerName = playerName;
			CommonGeneral.getInstance().getReportData().updateReport(this, "playerName");
		}
	}
	
	public boolean addReport(UUID playerReporting, String playerName, int reportLevel, String reason) {
		if (playersReason.containsKey(playerReporting))
			return false;
		
		playersReason.put(playerReporting, new ReportInformation(playerName, reason, reportLevel));
		reportExpire = System.currentTimeMillis() + (1000 * 60 * 60 * 12);
		lastReport = playerReporting;
		CommonGeneral.getInstance().getReportData().updateReport(this, "playersReason");
		CommonGeneral.getInstance().getReportData().updateReport(this, "reportExpire");
		CommonGeneral.getInstance().getReportData().updateReport(this, "lastReport");
		setReportLevel(getReportLevel() + reportLevel);
		return true;
	}

	public void expire() {
		CommonGeneral.getInstance().getReportData().deleteReport(getPlayerUniqueId());
		CommonGeneral.getInstance().getReportManager().unloadReport(getPlayerUniqueId());
	}
	
	public void banPlayer() {
		
		CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {
			
			@Override
			public void run() {
				
				for (Entry<UUID, ReportInformation> entry : playersReason.entrySet()) {
					Member member = CommonGeneral.getInstance().getMemberManager().getMember(entry.getKey());
					
					if (member == null) {
						MemberModel memberModel = CommonGeneral.getInstance().getPlayerData().loadMember(entry.getKey());
						
						if (memberModel == null)
							continue;
						
						memberModel = null;
						member = new MemberVoid(memberModel);
					}
					
					member.sendMessage("§a§l> §fO jogador §a" + getPlayerName() + "§f foi banido do servidor!");
					member.sendMessage("§a§l> §fVocê ganhou §a20 xp§f, §a50 money§f e §d2 ponto de reputação§f por ter reportado ele!");
					member.sendMessage("§a§l> §fObrigado por ajudar a comunidade do §aSaintMC§f!");
					member.setReputation(member.getReputation() + 2);
					member = null;
				}
				
			}
		});
		
		CommonGeneral.getInstance().getReportData().deleteReport(getPlayerUniqueId());
		CommonGeneral.getInstance().getReportManager().unloadReport(getPlayerUniqueId());
	}
	
	public void mutePlayer() {
		
		CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {
			
			@Override
			public void run() {
				
				for (Entry<UUID, ReportInformation> entry : playersReason.entrySet()) {
					Member member = CommonGeneral.getInstance().getMemberManager().getMember(entry.getKey());
					
					if (member == null) {
						MemberModel memberModel = CommonGeneral.getInstance().getPlayerData().loadMember(entry.getKey());
						
						if (memberModel == null)
							continue;
						
						memberModel = null;
						member = new MemberVoid(memberModel);
					}
					
					member.sendMessage("§a§l> §fO jogador §a" + getPlayerName() + "§f foi banido do servidor!");
					member.sendMessage("§a§l> §fVocê ganhou §a20 xp§f, §a50 money§f e §d1 ponto de reputação§f por ter reportado ele!");
					member.sendMessage("§a§l> §fObrigado por ajudar a comunidade do §aSaintMC§f!");
					member.setReputation(member.getReputation() + 1);
					member = null;
				}
				
			}
		});
		
		CommonGeneral.getInstance().getReportData().deleteReport(getPlayerUniqueId());
		CommonGeneral.getInstance().getReportManager().unloadReport(getPlayerUniqueId());
	}
	
	public void denyPlayer() {
//		DataReport.denyReport(this);
		CommonGeneral.getInstance().getReportData().deleteReport(getPlayerUniqueId());
		CommonGeneral.getInstance().getReportManager().unloadReport(getPlayerUniqueId());
	}

	public static class ReportInformation {
		private String playerName;
		private String reason;
		private long reportTime;
		private int reportLevel;
		private boolean rejected = false;

		public ReportInformation(String playerName, String reason, int reportLevel) {
			this.playerName = playerName;
			this.reason = reason;
			this.reportTime = System.currentTimeMillis();
			this.reportLevel = reportLevel;
		}

		public String getPlayerName() {
			return playerName;
		}

		public String getReason() {
			return reason;
		}
		
		public int getReportLevel() {
			return reportLevel;
		}

		public boolean isRejected() {
			return rejected;
		}

		public long getReportTime() {
			return reportTime;
		}

		public void reject() {
			rejected = true;
		}
	}
}
