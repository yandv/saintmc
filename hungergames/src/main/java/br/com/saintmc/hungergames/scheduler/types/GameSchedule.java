package br.com.saintmc.hungergames.scheduler.types;

import org.bukkit.event.Listener;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.scheduler.Schedule;

public interface GameSchedule extends Schedule, Listener {
	
	void registerListener();
	
	void unregisterListener();
	
	default void unload() {
		GameGeneral.getInstance().getSchedulerController().removeSchedule(this);
	}

}
