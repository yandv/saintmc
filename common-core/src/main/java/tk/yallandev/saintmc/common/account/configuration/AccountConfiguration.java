package tk.yallandev.saintmc.common.account.configuration;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.clan.enums.ClanDisplayType;

@Getter
public class AccountConfiguration {

	@Setter
	protected transient Member player;

	private boolean staffChatEnabled = false;
	private boolean tellEnabled = true;
	private boolean soundEnabled = true;
	private boolean reportEnabled = true;
	private boolean anticheatEnabled = true;
	private boolean scoreboardEnabled = true;
	private boolean clanChatEnabled = false;
	
	private boolean adminItems = true;
	private boolean adminOnJoin = true;
	
	private boolean seeingClanchat = false;
	private boolean seeingStaffchat = true;
	private boolean seeingStafflog = false;
	
	private ClanDisplayType clanDisplayType;

	public AccountConfiguration(Member player) {
		this.player = player;
	}

	public void setStaffChatEnabled(boolean staffChatEnabled) {
		if (this.staffChatEnabled != staffChatEnabled) {
			this.staffChatEnabled = staffChatEnabled;
			save();
		}
	}

	public void setSeeingStaffchat(boolean seeingStaffchat) {
		if (this.seeingStaffchat != seeingStaffchat) {
			this.seeingStaffchat = seeingStaffchat;
			save();
		}
	}

	public void setSeeingStafflog(boolean seeingStafflog) {
		if (this.seeingStafflog != seeingStafflog) {
			this.seeingStafflog = seeingStafflog;
			save();
		}
	}

	public void setTellEnabled(boolean tellEnabled) {
		if (this.tellEnabled != tellEnabled) {
			this.tellEnabled = tellEnabled;
			save();
		}
	}

	public void setSoundEnabled(boolean soundEnabled) {
		if (this.soundEnabled != soundEnabled) {
			this.soundEnabled = soundEnabled;
			save();
		}
	}

	public void setReportEnabled(boolean reportEnabled) {
		if (this.reportEnabled != reportEnabled) {
			this.reportEnabled = reportEnabled;
			save();
		}
	}

	public void setAnticheatEnabled(boolean anticheatEnabled) {
		if (this.anticheatEnabled != anticheatEnabled) {
			this.anticheatEnabled = anticheatEnabled;
			save();
		}
	}

	public void setAdminOnJoin(boolean adminOnJoin) {
		if (this.adminOnJoin != adminOnJoin) {
			this.adminOnJoin = adminOnJoin;
			save();
		}
	}

	public void setAdminItems(boolean adminItems) {
		if (this.adminItems != adminItems) {
			this.adminItems = adminItems;
			save();
		}
	}

	public void setScoreboardEnabled(boolean scoreboardEnabled) {
		if (this.scoreboardEnabled != scoreboardEnabled) {
			this.scoreboardEnabled = scoreboardEnabled;
			save();
		}
	}

	public void setClanDisplayType(ClanDisplayType clanDisplayType) {
		if (this.clanDisplayType != clanDisplayType) {
			this.clanDisplayType = clanDisplayType;
			save();
		}
	}
	
	public void setClanChatEnabled(boolean clanChatEnabled) {
		if (this.clanChatEnabled != clanChatEnabled) {
			this.clanChatEnabled = clanChatEnabled;
			save();
		}
	}
	
	public void setSeeingClatchat(boolean seeingClatchat) {
		if (this.seeingClanchat != seeingClatchat) {
			this.seeingClanchat = seeingClatchat;
			save();
		}
	}

	public ClanDisplayType getClanDisplayType() {
		if (clanDisplayType == null)
			clanDisplayType = ClanDisplayType.ALL;
		return clanDisplayType;
	}

	public void save() {
		if (player == null)
			return;

		CommonGeneral.getInstance().getPlayerData().updateMember(player, "accountConfiguration");
	}

}
