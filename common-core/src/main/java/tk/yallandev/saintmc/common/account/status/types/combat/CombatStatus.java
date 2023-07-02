package tk.yallandev.saintmc.common.account.status.types.combat;

import lombok.Getter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.status.Status;
import tk.yallandev.saintmc.common.account.status.StatusType;

import java.util.UUID;

@Getter
public class CombatStatus implements Status {

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

	public CombatStatus(CombatModel gameModel) {
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

		this.elo = gameModel.getElo();
	}

	public CombatStatus(UUID uniqueId, StatusType statusType) {
		this.uniqueId = uniqueId;
		this.statusType = statusType;
	}
	
	@Override
	public void setUniqueId(UUID uniqueId) {
		this.uniqueId = uniqueId;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "uniqueId");
	}

	public void setElo(long elo) {
		this.elo = elo;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "elo");
	}

	public void addElo(long elo) {
		this.elo += elo;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "elo");
	}

	public void removeElo(long elo) {
		this.elo -= elo;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "elo");
	}

	public long getElo() {
		return elo;
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
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "killstreak");
	}

	public void resetKillstreak() {
		this.killstreak = 0;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "killstreak");
	}

	public void setMatch(int games) {
		this.games = games;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "games");
	}

	public void addMatch() {
		games++;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "games");
	}

	public int getWins() {
		return wins;
	}

	public void addWin() {
		wins++;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "wins");
	}

	public void setWins(int win) {
		this.wins = win;
		CommonGeneral.getInstance().getStatusData().updateStatus(this, "wins");
	}

	public int getMaxKillstreak() {
		return maxStreak;
	}

	public int getMatches() {
		return games;
	}

}
