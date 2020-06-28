package tk.yallandev.saintmc.common.account.status.types.game;

import java.util.UUID;

import lombok.Getter;
import tk.yallandev.saintmc.common.account.status.StatusType;

@Getter
public class GameModel {
	
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
	
	public GameModel(GameStatus gameStatus) {
		this.uniqueId = gameStatus.getUniqueId();
		this.statusType = gameStatus.getStatusType();
		
		this.kills = gameStatus.getKills();
		this.deaths = gameStatus.getDeaths();
		this.killstreak = gameStatus.getKillstreak();
		
		this.games = gameStatus.getGames();
		this.losses = gameStatus.getLosses();
		this.wins = gameStatus.getWins();
		
		this.winStreak = gameStatus.getWinStreak();
		this.maxStreak = gameStatus.getMaxStreak();
	}

}
