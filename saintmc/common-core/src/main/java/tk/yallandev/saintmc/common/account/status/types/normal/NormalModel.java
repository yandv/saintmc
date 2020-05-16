package tk.yallandev.saintmc.common.account.status.types.normal;

import java.util.UUID;

import lombok.Getter;
import tk.yallandev.saintmc.common.account.status.StatusType;

@Getter
public class NormalModel {
	
	private UUID uniqueId;
	private StatusType statusType;
	
	private int kills;
	private int deaths;
	
	private int killstreak;
	private int maxKillstreak;
	
	public NormalModel(NormalStatus normalStatus) {
		this.uniqueId = normalStatus.getUniqueId();
		this.statusType = normalStatus.getStatusType();
		
		this.kills = normalStatus.getKills();
		this.deaths = normalStatus.getDeaths();
		this.killstreak = normalStatus.getKillstreak();
	}

}
