package tk.yallandev.saintmc.common.account.status.types.normal;

import java.util.UUID;

import lombok.Getter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.status.Status;
import tk.yallandev.saintmc.common.account.status.StatusType;

@Getter
public class NormalStatus implements Status {

	private UUID uniqueId;
	private StatusType statusType;

	private int kills;
	private int deaths;

	private int killstreak;
	private int maxKillstreak;

	public NormalStatus(NormalModel normalModel) {
		this.uniqueId = normalModel.getUniqueId();
		this.statusType = normalModel.getStatusType();

		this.kills = normalModel.getKills();
		this.deaths = normalModel.getDeaths();
		this.killstreak = normalModel.getKillstreak();
		this.maxKillstreak = normalModel.getMaxKillstreak();
	}

	public NormalStatus(UUID uniqueId, StatusType statusType) {
		this.uniqueId = uniqueId;
		this.statusType = statusType;
	}

	@Override
	public void setUniqueId(UUID uniqueId) {
		this.uniqueId = uniqueId;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "uniqueId");
	}

	public void setKills(int kills) {
		this.kills = kills;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "kills");
	}

	public void addKill() {
		this.kills++;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "kills");
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "deaths");
	}

	public void addDeath() {
		this.deaths++;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "deaths");
	}

	public void setKillstreak(int killstreak) {
		this.killstreak = killstreak;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "killstreak");
	}

	public void addKillstreak() {
		this.killstreak++;

		if (this.killstreak > this.maxKillstreak) {
			this.maxKillstreak++;
			CommonGeneral.getInstance().getStatusData().updateStatus(this, "maxKillstreak");
		}

		CommonGeneral.getInstance().getStatusData().updateStatus(this, "killstreak");
	}

	public void resetKillstreak() {
		if (this.killstreak > this.maxKillstreak) {
			this.maxKillstreak = killstreak;
			CommonGeneral.getInstance().getStatusData().updateStatus(this, "maxKillstreak");
		}

		this.killstreak = 0;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "killstreak");
	}

}
