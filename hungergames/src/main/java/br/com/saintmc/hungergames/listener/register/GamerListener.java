package br.com.saintmc.hungergames.listener.register;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.listener.GameListener;
import br.com.saintmc.hungergames.utils.ServerConfig;

public class GamerListener extends GameListener {
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		if (event.getLoginResult() != Result.ALLOWED)
			return;
		
		if(!ServerConfig.getInstance().isJoinEnabled())
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cO servidor está carregando!");
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (event.getResult() != PlayerLoginEvent.Result.ALLOWED)
			return;

		Player player = event.getPlayer();
		
		Gamer gamer = new Gamer(player);
		GameGeneral.getInstance().getGamerController().loadGamer(player.getUniqueId(), gamer);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (GameGeneral.getInstance().getGamerController().getGamer(event.getPlayer().getUniqueId()) == null) {
			event.getPlayer().kickPlayer("§cConta não carregada!");
			return;
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (isPregame()) {
			GameGeneral.getInstance().getGamerController().unload(event.getPlayer().getUniqueId());
		} else {
			Player player = event.getPlayer();
			Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

			if (gamer.isNotPlaying()) {
				event.setQuitMessage(null);
				return;
			}
		}
	}

}
