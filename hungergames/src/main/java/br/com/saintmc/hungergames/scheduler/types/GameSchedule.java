package br.com.saintmc.hungergames.scheduler.types;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.scheduler.Schedule;

public interface GameSchedule extends Schedule {
	
	void registerListener();
	
	void unregisterListener();
	
	default void unload() {
		GameGeneral.getInstance().getSchedulerController().removeSchedule(this);
	}

}
