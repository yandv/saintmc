package tk.yallandev.saintmc.skwyars.scheduler.types;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import lombok.Getter;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.skwyars.GameGeneral;
import tk.yallandev.saintmc.skwyars.event.game.GameStartEvent;
import tk.yallandev.saintmc.skwyars.listener.WaitingListener;

public class WaitingScheduler implements GameSchedule {

	private GameGeneral gameGeneral;
	@Getter
	private List<Listener> listenerList;

	public WaitingScheduler() {
		this.gameGeneral = GameGeneral.getInstance();
		this.listenerList = Arrays.asList(new WaitingListener());
		registerListener();
	}

	@Override
	public void pulse(int time, MinigameState gameState) {

		if (time <= 0) {
			gameGeneral.setGameState(MinigameState.STARTING);

			Bukkit.getPluginManager().callEvent(new GameStartEvent());
			unregisterListener();
			unregister();
			return;
		}

		if (time == 60 || time == 45 || time == 30 || time == 15 || (time <= 5)) {
			Bukkit.broadcastMessage("§eO jogo inicia em §b" + time + " segundos§e!");
		}
	}

}
