package tk.yallandev.saintmc.skwyars.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import tk.yallandev.saintmc.skwyars.GameGeneral;
import tk.yallandev.saintmc.skwyars.GameMain;
import tk.yallandev.saintmc.skwyars.scheduler.Schedule;

public class SchedulerController {

	private List<Schedule> scheduleList;

	public SchedulerController() {
		scheduleList = new ArrayList<>();
	}

	public void addSchedule(Schedule schedule) {
		if (!scheduleList.contains(schedule)) {
			scheduleList.add(schedule);

			if (Listener.class.isAssignableFrom(schedule.getClass())) {
				Bukkit.getPluginManager().registerEvents((Listener) schedule, GameMain.getInstance());
			}

		}
	}

	public void removeSchedule(Schedule schedule) {
		if (scheduleList.contains(schedule)) {
			scheduleList.remove(schedule);

			if (Listener.class.isAssignableFrom(schedule.getClass())) {
				HandlerList.unregisterAll((Listener) schedule);
			}
		}
	}

	public void pulse() {
		Iterator<Schedule> iterator = scheduleList.iterator();

		while (iterator.hasNext()) {
			try {
				iterator.next().pulse(GameGeneral.getInstance().getTime(),
						GameGeneral.getInstance().getMinigameState());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
