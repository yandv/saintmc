package tk.yallandev.saintmc.skwyars.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import tk.yallandev.saintmc.skwyars.GameGeneral;
import tk.yallandev.saintmc.skwyars.gamer.Gamer;

public class GamerListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (event.getResult() != PlayerLoginEvent.Result.ALLOWED)
			return;

		GameGeneral.getInstance().getGamerController().loadGamer(new Gamer(event.getPlayer()));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		GameGeneral.getInstance().getGamerController().unload(event.getPlayer().getUniqueId());
	}

}
