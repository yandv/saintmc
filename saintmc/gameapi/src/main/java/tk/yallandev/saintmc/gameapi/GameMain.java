package tk.yallandev.saintmc.gameapi;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.gameapi.event.game.GameTimerEvent;
import tk.yallandev.saintmc.gameapi.games.GameType;
import tk.yallandev.saintmc.gameapi.manager.SchedulerManager;

@Getter
public class GameMain extends JavaPlugin {
	
	@Getter
	private static GameMain instance;
	
	private SchedulerManager schedulerManager;
	
	private GamerManager gamerManager;
	
	/*
	 * Game Info
	 */
	
	private GameType gameType = GameType.NONE;
	private MinigameState gameStage = MinigameState.NONE;
	private int time;
	private TimerType timerType;
	
	private GameMode gameMode;
	
	@Override
	public void onLoad() {
		super.onLoad();
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
	}
	
	public void setTimer(int time) {
		getServer().getPluginManager().callEvent(new GameTimerEvent());
		this.time = time;
	}

	public void setTimerType(TimerType timerType) {
		this.timerType = timerType;
	}

	public void count() {
		if (timerType == TimerType.COUNT_UP)
			setTimer(time + 1);
		else if (timerType == TimerType.COUNTDOWN)
			setTimer(time - 1);
	}
	
	public static GameMain getPlugin() {
		return instance;
	}
	
	public enum TimerType {
		
		COUNT_UP, COUNTDOWN, STOP;
		
	}

}
