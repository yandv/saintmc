package br.com.saintmc.hungergames.controller;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.scheduler.Schedule;

public class SchedulerController {

	private List<Schedule> scheduleList;

	public SchedulerController() {
		scheduleList = new ArrayList<>();
	}

	public void addSchedule(Schedule schedule) {
		if (!scheduleList.contains(schedule)) {
			scheduleList.add(schedule);
			Bukkit.getPluginManager().registerEvents(schedule, GameMain.getInstance());
		}
	}

	public void removeSchedule(Schedule schedule) {
		if (scheduleList.contains(schedule)) {
			scheduleList.remove(schedule);
			HandlerList.unregisterAll(schedule);
		}
	}
	
	public void pulse() {
		for (Schedule schedule : scheduleList) {
			try {
				schedule.pulse(GameGeneral.getInstance().getTime(), GameGeneral.getInstance().getGameState());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
