package br.com.saintmc.hungergames.scheduler.types;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.game.GameState;
import br.com.saintmc.hungergames.listener.register.game.CombatlogListener;
import br.com.saintmc.hungergames.listener.register.winner.WinnerListener;
import tk.yallandev.saintmc.bukkit.event.admin.PlayerAdminModeEvent;

public class GameScheduler implements GameSchedule {
	
	private GameGeneral gameGeneral;
	private List<Listener> listenerList;

	public GameScheduler() {
		this.gameGeneral = GameGeneral.getInstance();
		this.listenerList = Arrays.asList(new CombatlogListener());
		
		registerListener();
		checkWin();
	}
	
	@Override
	public void pulse(int time, GameState gameState) {
		
		if (time >= 60*60) {
			
		}
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerAdminMode(PlayerAdminModeEvent event) {
		checkWin();
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerAdminMode(PlayerDeathEvent event) {
		checkWin();
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerAdminMode(PlayerQuitEvent event) {
		checkWin();
	}
	
	public void checkWin() {
		
		if (gameGeneral.getPlayersInGame() > 1) {
			return;
		}

		Player pWin = null;
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			Gamer gamer = gameGeneral.getGamerController().getGamer(p);

			if (gamer.isGamemaker())
				continue;

			if (gamer.isSpectator())
				continue;

			if (!p.isOnline())
				continue;

			pWin = p;
			break;
		}

		unregisterListener();
		GameGeneral.getInstance().getSchedulerController().removeSchedule(this);
		GameMain.getInstance().registerListener(new WinnerListener(pWin));
	}

	@Override
	public void registerListener() {
		listenerList.forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, GameMain.getInstance()));
	}

	@Override
	public void unregisterListener() {
		listenerList.forEach(listener -> HandlerList.unregisterAll(listener));
	}

}
