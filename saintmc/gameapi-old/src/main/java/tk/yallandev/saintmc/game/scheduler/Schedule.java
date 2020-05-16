package tk.yallandev.saintmc.game.scheduler;

import tk.yallandev.saintmc.game.constructor.ScheduleArgs;

public interface Schedule {

	public void pulse(ScheduleArgs args);
}
