package tk.yallandev.saintmc.common.backend.data;

import java.util.Collection;
import java.util.UUID;

import tk.yallandev.saintmc.common.ban.constructor.Ban;
import tk.yallandev.saintmc.common.ban.constructor.Mute;
import tk.yallandev.saintmc.common.ban.constructor.Warn;

public interface PunishData {
	
	/*
	 * Ban
	 */
	
	Collection<Ban> loadBan(UUID uniqueId, int limit);
	
	boolean hasBan(UUID uniqueId);
	
	void addBan(Ban ban);
	
	int countBan();
	
	/*
	 * Mute
	 */
	
	Collection<Mute> loadMute(UUID uniqueId, int limit);
	
	boolean hasMute(UUID uniqueId);
	
	void addMute(Mute mute);
	
	int countMute();
	
	/*
	 * Warn
	 */
	
	Collection<Warn> loadWarn(UUID uniqueId, int limit);
	
	boolean hasWarn(UUID uniqueId);
	
	void addWarn(Warn warn);
	
	int countWarn();
	
}
