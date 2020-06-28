package tk.yallandev.saintmc.kitpvp.party;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import tk.yallandev.saintmc.kitpvp.gamer.Gamer;

public interface Party extends Listener {
	
	/**
	 * Start the minigame
	 */
	
	void start();
	
	/**
	 * Force end the event
	 */
	
	void forceEnd(Player winner);
	
	/**
	 * 
	 * Join a Gamer 
	 * 
	 * @param gamer
	 * @return
	 */
	
	boolean join(Gamer gamer);
	
	/**
	 * 
	 * Set a player like spectate
	 * 
	 * @param gamer
	 * @return
	 */
	
	boolean spectate(Gamer gamer);
	
	/**
	 * 
	 * Leave a Gamer
	 * 
	 * @param gamer
	 * @return
	 */
	
	void leave(Player player);
	
	/**
	 * 
	 * Check if the full
	 * 
	 * @return
	 */
	
	boolean isFull();
	
	/**
	 * 
	 * Return the time of the event
	 * 
	 * @return
	 */
	
	int getTime();
	
	/**
	 * 
	 * Change the time of the party
	 * 
	 * @return
	 */
	
	void setTime(int time);
	
	/**
	 * 
	 * Check 
	 * 
	 * @return
	 */
	
	boolean hasStarted();

}
