package tk.yallandev.saintmc.skwyars;

import org.bukkit.Bukkit;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.skwyars.controller.AbilityController;
import tk.yallandev.saintmc.skwyars.controller.GamerController;
import tk.yallandev.saintmc.skwyars.controller.LocationController;
import tk.yallandev.saintmc.skwyars.controller.SchedulerController;
import tk.yallandev.saintmc.skwyars.controller.TeamController;
import tk.yallandev.saintmc.skwyars.event.game.GameStateChangeEvent;
import tk.yallandev.saintmc.skwyars.event.game.GameTimeEvent;
import tk.yallandev.saintmc.skwyars.scheduler.types.WaitingScheduler;

@Getter
public class GameGeneral {
	
	@Getter
	private static GameGeneral instance;
	
	private GamerController gamerController;
	private SchedulerController schedulerController;
	private LocationController locationController;
	private AbilityController abilityController;
	private TeamController teamController;
	
	private MinigameState minigameState = MinigameState.WAITING;
	private int time = getDefaultTime(getMinigameState());
	@Setter
	private boolean countTime;
	
	public void onLoad() {
		instance = this;
	}
	
	public void onEnable() {
		gamerController = new GamerController();
		
		schedulerController = new SchedulerController();
		schedulerController.addSchedule(new WaitingScheduler());
		
		abilityController = new AbilityController();
		abilityController.registerKits();
		
		locationController = new LocationController();
		
		teamController = new TeamController();
	}
	
	public void onDisable() {
		
	}

	public void setGameState(MinigameState minigameState) {
		Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(this.minigameState, minigameState));
		this.minigameState = minigameState;
		this.time = getDefaultTime(minigameState);
	}

	public void setGameState(MinigameState minigameState, boolean defaultTime) {
		Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(this.minigameState, minigameState));
		this.minigameState = minigameState;

		if (defaultTime)
			this.time = getDefaultTime(minigameState);
	}

	public void setTime(int time) {
		Bukkit.getPluginManager().callEvent(new GameTimeEvent(time));
		this.time = time;
	}

	public void pulse() {
		if (isCountTime()) {
			schedulerController.pulse();

			if (isUpTime(minigameState))
				setTime(getTime() + 1);
			else
				setTime(getTime() - 1);
		}
	}
	
	private boolean isUpTime(MinigameState minigameState) {
		return !minigameState.isDecrementTime();
	}

	private int getDefaultTime(MinigameState minigameState) {
		switch (minigameState) {
		case STARTING:
			return 10;
		case WAITING:
			return 60;
		case GAMETIME:
			return 1;
		default:
			return 0;
		}
	}
}
