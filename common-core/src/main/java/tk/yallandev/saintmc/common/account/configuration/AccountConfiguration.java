package tk.yallandev.saintmc.common.account.configuration;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.Member;

public class AccountConfiguration {

	@Setter
	protected transient Member player;

	@Getter
	private boolean staffChatEnabled = false;

	@Getter
	private boolean tellEnabled = true;
	
	@Getter
	private boolean soundEnabled = true;
	
	@Getter
	private boolean reportEnabled = true;
	
	@Getter
	private boolean anticheatEnabled = true;
	
	@Getter
	private boolean scoreboardEnabled = true;
	
	@Getter
	private boolean adminItems = true;
	
	@Getter
	private boolean adminOnJoin = true;
	
	public AccountConfiguration(Member player) {
		this.player = player;
	}

	public void setStaffChatEnabled(boolean staffChatEnabled) {
		if (this.staffChatEnabled != staffChatEnabled) {
			this.staffChatEnabled = staffChatEnabled;
			CommonGeneral.getInstance().getPlayerData().updateMember(player, "accountConfiguration");
		}
	}

	public void setTellEnabled(boolean tellEnabled) {
		if (this.tellEnabled != tellEnabled) {
			this.tellEnabled = tellEnabled;
			CommonGeneral.getInstance().getPlayerData().updateMember(player, "accountConfiguration");
		}
	}
	
	public void setSoundEnabled(boolean soundEnabled) {
		if (this.soundEnabled != soundEnabled) {
			this.soundEnabled = soundEnabled;
			CommonGeneral.getInstance().getPlayerData().updateMember(player, "accountConfiguration");
		}
	}
	
	public void setReportEnabled(boolean reportEnabled) {
		if (this.reportEnabled != reportEnabled) {
			this.reportEnabled = reportEnabled;
			CommonGeneral.getInstance().getPlayerData().updateMember(player, "accountConfiguration");
		}
	}
	
	public void setAnticheatEnabled(boolean anticheatEnabled) {
		if (this.anticheatEnabled != anticheatEnabled) {
			this.anticheatEnabled = anticheatEnabled;
			CommonGeneral.getInstance().getPlayerData().updateMember(player, "accountConfiguration");
		}
	}
	
	public void setAdminOnJoin(boolean adminOnJoin) {
		if (this.adminOnJoin != adminOnJoin) {
			this.adminOnJoin = adminOnJoin;
			CommonGeneral.getInstance().getPlayerData().updateMember(player, "accountConfiguration");
		}
	}
	
	public void setAdminItems(boolean adminItems) {
		if (this.adminItems != adminItems) {
			this.adminItems = adminItems;
			CommonGeneral.getInstance().getPlayerData().updateMember(player, "accountConfiguration");
		}
	}
	
	public void setScoreboardEnabled(boolean scoreboardEnabled) {
		if (this.scoreboardEnabled != scoreboardEnabled) {
			this.scoreboardEnabled = scoreboardEnabled;
			CommonGeneral.getInstance().getPlayerData().updateMember(player, "accountConfiguration");
		}
	}
	
}
