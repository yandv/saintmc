package tk.yallandev.saintmc.common.account.status.types.combat;

import lombok.Getter;
import tk.yallandev.saintmc.common.account.status.StatusType;

import java.util.UUID;

@Getter
public class CombatModel {
	
	private UUID uniqueId;
	private StatusType statusType;
	
	private int kills;
	private int deaths;
	private int killstreak;
	
	private int games;
	private int losses;
	private int wins;
	
	private int winStreak;
	private int maxStreak;

	private long elo = 1000;
	
	public CombatModel(CombatStatus combatStatus) {
		this.uniqueId = combatStatus.getUniqueId();
		this.statusType = combatStatus.getStatusType();
		
		this.kills = combatStatus.getKills();
		this.deaths = combatStatus.getDeaths();
		this.killstreak = combatStatus.getKillstreak();
		
		this.games = combatStatus.getGames();
		this.losses = combatStatus.getLosses();
		this.wins = combatStatus.getWins();
		
		this.winStreak = combatStatus.getWinStreak();
		this.maxStreak = combatStatus.getMaxStreak();

		this.elo = combatStatus.getElo();
	}

}
