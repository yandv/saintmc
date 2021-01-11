package tk.yallandev.saintmc.skwyars.listener.game;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import tk.yallandev.saintmc.skwyars.GameGeneral;
import tk.yallandev.saintmc.skwyars.GameMain;
import tk.yallandev.saintmc.skwyars.event.game.GameTimeEvent;
import tk.yallandev.saintmc.skwyars.game.EventType;

public class EventListener implements Listener {

	private int refilTimes;

	@EventHandler
	public void onGameTime(GameTimeEvent event) {
		if (GameMain.getInstance().getGameGeneral().getEventController().pulse()) {
			EventType eventType = GameMain.getInstance().getGameGeneral().getEventController().getEventType();

			if (eventType == EventType.REFIL) {
				Bukkit.broadcastMessage("§bOs baus foram preenchidos novamente!");

				Bukkit.getWorlds().forEach(
						world -> GameGeneral.getInstance().getLocationController().getChestList().forEach(chest -> {
							chest.fill(world);
						}));

				refilTimes++;

				if (refilTimes == 1)
					GameMain.getInstance().getGameGeneral().getEventController().setEventType(EventType.REFIL, 120);
				else
					GameMain.getInstance().getGameGeneral().getEventController().setEventType(EventType.DEATHMATCH,
							240);
			} else if (eventType == EventType.DEATHMATCH) {
				Bukkit.getWorlds().forEach(world -> {
					world.getWorldBorder().setCenter(0, 0);
					world.getWorldBorder().setSize(20);
				});

				Bukkit.getOnlinePlayers().forEach(player -> {
					player.teleport(GameMain.getInstance().getLocationFromConfig("deathmatch"));
					player.setNoDamageTicks(20 * 10);
					player.sendMessage(" ");
					player.sendMessage("§aVocê está invencível por 10 segundos!");
					player.sendMessage("§aPrepare-se para batalhar pela vitória!");
					player.sendMessage(" ");
				});

				GameMain.getInstance().getGameGeneral().getEventController().setEventType(EventType.FINISH, 120);
				/* px todos p arena e é isso */
			} else if (eventType == EventType.FINISH) {
				Bukkit.shutdown();
			}
		}
	}

}
