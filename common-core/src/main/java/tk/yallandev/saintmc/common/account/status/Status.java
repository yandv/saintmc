package tk.yallandev.saintmc.common.account.status;

import java.util.UUID;

public interface Status {

	UUID getUniqueId();

	StatusType getStatusType();
	
	/*
	 * Kills
	 */

	void setKills(int kills);

	void addKill();

	int getKills();
	
	/*
	 * Death
	 */

	void setDeaths(int deaths);

	void addDeath();

	int getDeaths();
	
	/*
	 * Killstreak
	 */

	int getKillstreak();

	void setKillstreak(int killstreak);

	void addKillstreak();

	void resetKillstreak();

	int getMaxKillstreak();
	
	/*
	 * Match
	 */

	int getMatches();

	void setMatch(int match);
	
	void addMatch();
	
	/*
	 * Win
	 */

	int getWins();
	
	void setWins(int win);

	void addWin();

}
