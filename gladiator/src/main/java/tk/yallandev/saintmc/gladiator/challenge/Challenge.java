package tk.yallandev.saintmc.gladiator.challenge;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Challenge {
	
	boolean isExpired();
	
	long getExpire();
	
	void start(Location firstLocation, Location secondLocation);
	
	void finish(Player player);
	
	ChallengeType getChallengeType();

}
