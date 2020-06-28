package tk.yallandev.saintmc.skwyars.scheduler.types;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import tk.yallandev.saintmc.skwyars.GameGeneral;
import tk.yallandev.saintmc.skwyars.GameMain;
import tk.yallandev.saintmc.skwyars.scheduler.Schedule;

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
	
	List<Listener> getListenerList();

	default void registerListener() {
		getListenerList().forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, GameMain.getInstance()));
	}

	default void unregisterListener() {
		getListenerList().forEach(listener -> HandlerList.unregisterAll(listener));
	}

	default void unload() {
		GameGeneral.getInstance().getSchedulerController().removeSchedule(this);
	}

}
