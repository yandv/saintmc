package tk.yallandev.saintmc.kitpvp.warp.challenge;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Challenge {
	
	boolean isExpired();
	
	long getExpire();
	
	void start(Location firstLocation, Location secondLocation);
	
	void finish(Player player);
	
	boolean isInChallenge(Player player);
	
	ChallengeType getChallengeType();

}
