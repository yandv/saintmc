package br.com.saintmc.hungergames.scheduler.types;

import org.bukkit.event.Listener;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.scheduler.Schedule;

/**
 * 
 * GameSchedule is a class that will assist me to make a GameScheduler like
 * PregameScheduler
 * 
 * @author yandv
 *
 */

public interface GameSchedule extends Schedule, Listener {
	
	/**
	 * 
	 * Register a listenerList 
	 * 
	 */

	void registerListener();

	void unregisterListener();

	default void unload() {
		GameGeneral.getInstance().getSchedulerController().removeSchedule(this);
	}

}
