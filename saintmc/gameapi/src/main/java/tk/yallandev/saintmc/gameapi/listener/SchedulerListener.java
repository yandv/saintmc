package tk.yallandev.saintmc.gameapi.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.gameapi.GameMain;

public class SchedulerListener implements Listener {

	private GameMain main;

	public SchedulerListener(GameMain main) {
		this.main = main;
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() != UpdateType.SECOND)
			return;
		
		main.getSchedulerManager().pulse();
		main.count();
	}

}
