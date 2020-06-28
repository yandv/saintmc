package tk.yallandev.saintmc.skwyars.scheduler.types;

import java.util.Arrays;
import java.util.List;

import org.bukkit.event.Listener;

import lombok.Getter;
import tk.yallandev.saintmc.skwyars.GameGeneral;
import tk.yallandev.saintmc.skwyars.listener.SpectatorListener;
import tk.yallandev.saintmc.skwyars.listener.game.EventListener;
import tk.yallandev.saintmc.skwyars.listener.game.GameListener;
import tk.yallandev.saintmc.skwyars.listener.game.WinnerListener;
import tk.yallandev.saintmc.skwyars.scheduler.MinigameState;

public class GameScheduler implements GameSchedule {

	private GameGeneral gameGeneral;
	@Getter
	private List<Listener> listenerList;

	public GameScheduler() {
		this.gameGeneral = GameGeneral.getInstance();
		this.listenerList = Arrays.asList(new GameListener(), new EventListener(), new WinnerListener(),
				new SpectatorListener());
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
