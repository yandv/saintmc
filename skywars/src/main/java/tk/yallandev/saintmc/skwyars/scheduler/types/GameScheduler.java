package tk.yallandev.saintmc.skwyars.scheduler.types;

import java.util.Arrays;
import java.util.List;

import org.bukkit.event.Listener;

import lombok.Getter;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.skwyars.GameGeneral;
import tk.yallandev.saintmc.skwyars.listener.GameListener;

public class GameScheduler implements GameSchedule {

	private GameGeneral gameGeneral;
	@Getter
	private List<Listener> listenerList;

	public GameScheduler() {
		this.gameGeneral = GameGeneral.getInstance();
		this.listenerList = Arrays.asList(new GameListener());
		registerListener();
	}

	@Override
	public void pulse(int time, MinigameState gameState) {

		if (gameState == MinigameState.STARTING) {
			if (time == 0) {
				gameGeneral.setGameState(MinigameState.GAMETIME);
			}
		}

	}

}
