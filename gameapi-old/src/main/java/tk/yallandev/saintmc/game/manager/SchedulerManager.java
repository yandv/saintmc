package tk.yallandev.saintmc.game.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.constructor.ScheduleArgs;
import tk.yallandev.saintmc.game.scheduler.Schedule;

public class SchedulerManager {
	
	private Map<String, Schedule> schedules;
	private GameMain main;

	public SchedulerManager(GameMain main) {
		this.main = main;
		schedules = new HashMap<>();
	}

	public void pulse() {
		Iterator<Schedule> iterator = new ArrayList<>(schedules.values()).iterator();
		while (iterator.hasNext()) {
			Schedule schedule = iterator.next();
			try {
				schedule.pulse(new ScheduleArgs(main.getGameType(), main.getGameStage(), main.getTimer()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addScheduler(String id, Schedule schedule) {
		if (schedules.containsKey(id))
			return;
		schedules.put(id, schedule);
	}

	public void cancelScheduler(String id) {
		schedules.remove(id);
	}

	public Collection<Schedule> getSchedules() {
		return schedules.values();
	}

}
