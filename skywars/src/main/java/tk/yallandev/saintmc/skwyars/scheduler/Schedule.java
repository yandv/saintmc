package tk.yallandev.saintmc.skwyars.scheduler;

import tk.yallandev.saintmc.skwyars.GameGeneral;

public interface Schedule {
	
	void pulse(int time, MinigameState gameState);
	
	default void unregister() {
		GameGeneral.getInstance().getSchedulerController().removeSchedule(this);
	}

}
