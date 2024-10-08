package tk.yallandev.saintmc.skwyars.scheduler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.skwyars.GameGeneral;
import tk.yallandev.saintmc.skwyars.event.game.GameStartEvent;
import tk.yallandev.saintmc.skwyars.scheduler.types.GameScheduler;

public class SchedulerListener implements Listener {

	private GameGeneral gameGeneral;

	public SchedulerListener() {
		this.gameGeneral = GameGeneral.getInstance();
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() != UpdateType.SECOND)
			return;

		gameGeneral.pulse();
	}

	@EventHandler
	public void onGameStart(GameStartEvent event) {
		gameGeneral.getSchedulerController().addSchedule(new GameScheduler());
	}

}
