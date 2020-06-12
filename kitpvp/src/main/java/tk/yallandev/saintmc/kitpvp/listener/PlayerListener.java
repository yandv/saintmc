package tk.yallandev.saintmc.kitpvp.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.gamer.Gamer;

public class PlayerListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerLoginEvent event) {
		if (event.getResult() != Result.ALLOWED)
			return;
		
		Player player = event.getPlayer();
		Gamer gamer = new Gamer(player);

		GameMain.getInstance().getGamerManager().loadGamer(player.getUniqueId(), gamer);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		GameMain.getInstance().getGamerManager().unloadGamer(event.getPlayer().getUniqueId());
		event.setQuitMessage(null);
	}

}
