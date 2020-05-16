package tk.yallandev.saintmc.game.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.gameevents.gamer.GamerQuitEvent;

public class QuitListener extends GameListener {

	public QuitListener(GameMain main) {
		super(main);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent event) {
		getGameMain().getGameEventManager().newEvent(new GamerQuitEvent(event.getPlayer().getUniqueId()));
	}

}
