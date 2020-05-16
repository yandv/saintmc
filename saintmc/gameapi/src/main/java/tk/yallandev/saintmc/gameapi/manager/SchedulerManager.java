package tk.yallandev.saintmc.gameapi.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import tk.yallandev.saintmc.gameapi.GameMain;
import tk.yallandev.saintmc.gameapi.scheduler.Schedule;

/**
 * 
 * Based in gameapi-old timer strategy
 * 
 * @author Allan
 *
 */

public class SchedulerManager {
	
	private GameMain gameMain;
	
	private Map<String, Schedule> scheduleMap;
	
	public SchedulerManager(GameMain gameMain) {
		this.gameMain = gameMain;
		this.scheduleMap = new HashMap<>();
	}
	
	public void pulse() {
		Iterator<Schedule> iterator = scheduleMap.values().iterator();
		
		while (iterator.hasNext()) {
			Schedule schedule = iterator.next();
			
			try {
				schedule.pulse(gameMain.getGameType(), gameMain.getGameStage(), gameMain.getTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
