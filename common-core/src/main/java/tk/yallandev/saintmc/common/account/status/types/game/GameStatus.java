package tk.yallandev.saintmc.common.account.status.types.game;

import java.util.UUID;

import lombok.Getter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.status.Status;
import tk.yallandev.saintmc.common.account.status.StatusType;

@Getter
public class GameStatus implements Status {
	
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
	
	public GameStatus(GameModel gameModel) {
		this.uniqueId = gameModel.getUniqueId();
		this.statusType = gameModel.getStatusType();
		
		this.kills = gameModel.getKills();
		this.deaths = gameModel.getDeaths();
		this.killstreak = gameModel.getKillstreak();
		
		this.games = gameModel.getGames();
		this.losses = gameModel.getLosses();
		this.wins = gameModel.getWins();
		
		this.winStreak = gameModel.getWinStreak();
		this.maxStreak = gameModel.getMaxStreak();
	}
	
	public GameStatus(UUID uniqueId, StatusType statusType) {
		this.uniqueId = uniqueId;
		this.statusType = statusType;
	}
	
	@Override
	public void setKills(int kills) {
		this.kills = kills;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "kills");
	}
	
	@Override
	public void addKill() {
		this.kills++;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "kills");
	}

	@Override
	public void setDeaths(int deaths) {
		this.deaths = deaths;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "deaths");
	}
	
	@Override
	public void addDeath() {
		this.deaths++;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "deaths");
	}

	@Override
	public void setKillstreak(int killstreak) {
		this.killstreak = killstreak;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "killstreak");
	}
	
	@Override
	public void addKillstreak() {
		this.killstreak++;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "killstreak");
	}
	
	@Override
	public void resetKillstreak() {
		this.killstreak = 0;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "killstreak");
	}
	
	@Override
	public int getMaxKillstreak() {
		return maxStreak;
	}

	@Override
	public int getMatches() {
		return games;
	}
	
	@Override
	public void setMatch(int games) {
		this.games = games;
	}
	
	@Override
	public void addMatch() {
		games++;
	}

	@Override
	public int getWins() {
		return wins;
	}

	@Override
	public void addWin() {
		wins++;
	}

	@Override
	public void setWins(int win) {
		this.wins = win;
	}

}