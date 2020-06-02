package br.com.saintmc.hungergames;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import br.com.saintmc.hungergames.controller.AbilityController;
import br.com.saintmc.hungergames.controller.GamerController;
import br.com.saintmc.hungergames.controller.KitController;
import br.com.saintmc.hungergames.controller.SchedulerController;
import br.com.saintmc.hungergames.controller.SimplekitController;
import br.com.saintmc.hungergames.controller.TimeoutController;
import br.com.saintmc.hungergames.event.game.GameStateChangeEvent;
import br.com.saintmc.hungergames.event.game.GameTimeEvent;
import br.com.saintmc.hungergames.game.GameState;
import br.com.saintmc.hungergames.scheduler.types.PregameScheduler;
import lombok.Getter;
import lombok.Setter;

@Getter
public class GameGeneral {

	@Getter
	private static GameGeneral instance;

	private GameState gameState;
	private int time;
	@Setter
	private boolean countTime;

	private GamerController gamerController;

	private AbilityController abilityController;
	private KitController kitController;
	private SchedulerController schedulerController;
	
	private SimplekitController simplekitController;
	private TimeoutController timeoutController;

	public GameGeneral() {
		instance = this;

		gameState = GameState.WAITING;
		time = gameState.getDefaultTime();

		gamerController = new GamerController();
		
		abilityController = new AbilityController();
		kitController = new KitController();
		schedulerController = new SchedulerController();
		
		simplekitController = new SimplekitController();
		timeoutController = new TimeoutController();
	}

	public void onLoad() {
		
//		MapUtils.deleteWorld("world");
		
	}

	public void onEnable() {
		
		abilityController.load("br.com.saintmc.hungergames.abilities.register");
		kitController.load("br.com.saintmc.hungergames.kit.register");
		schedulerController.addSchedule(new PregameScheduler());

	}

	public void onDisable() {
		
	}

	public void setGameState(GameState gameState) {
		Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(this.gameState, gameState));
		this.gameState = gameState;
		this.time = gameState.getDefaultTime();
	}

	public void setTime(int time) {
		Bukkit.getPluginManager().callEvent(new GameTimeEvent(time));
		this.time = time;
	}

	public void pulse() {
		schedulerController.pulse();

		if (isCountTime()) {
			if (gameState.isUpTime())
				setTime(getTime() + 1);
			else
				setTime(getTime() - 1);
		}
	}

	public int getPlayersInGame() {
		return gamerController.getStoreMap().values().stream()
				.filter(gamer -> !gamer.isNotPlaying()).collect(Collectors.toList()).size();
	}

}
