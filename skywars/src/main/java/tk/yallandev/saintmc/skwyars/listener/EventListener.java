package tk.yallandev.saintmc.skwyars.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import tk.yallandev.saintmc.skwyars.GameGeneral;
import tk.yallandev.saintmc.skwyars.GameMain;
import tk.yallandev.saintmc.skwyars.event.game.GameTimeEvent;
import tk.yallandev.saintmc.skwyars.game.EventType;
import tk.yallandev.saintmc.skwyars.game.chest.Chest;

public class EventListener implements Listener {

	private int refilTimes;

	@EventHandler
	public void onGameTime(GameTimeEvent event) {
		if (GameMain.getInstance().getGameGeneral().getEventController().pulse()) {
			EventType eventType = GameMain.getInstance().getGameGeneral().getEventController().getEventType();

			if (eventType == EventType.REFIL) {
				Bukkit.broadcastMessage("§bOs baus foram preenchidos novamente!");

				for (Chest chest : GameGeneral.getInstance().getLocationController().getChestList()) {
					chest.getChest(Bukkit.getWorlds().stream().findFirst().orElse(null)).getBlockInventory().clear();
					chest.fill(Bukkit.getWorlds().stream().findFirst().orElse(null));
				}

				refilTimes++;

				if (refilTimes == 2)
					GameMain.getInstance().getGameGeneral().getEventController().setEventType(EventType.REFIL, 120);
				else
					GameMain.getInstance().getGameGeneral().getEventController().setEventType(EventType.DEATHMATCH,
							240);
			} else if (eventType == EventType.DEATHMATCH) {
				GameMain.getInstance().getGameGeneral().getEventController().setEventType(EventType.FINISH,
						120);
				
				/* px todos p arena e é isso*/
			} else if (eventType == EventType.FINISH) {
				Bukkit.shutdown();
			}
		}
	}

}
