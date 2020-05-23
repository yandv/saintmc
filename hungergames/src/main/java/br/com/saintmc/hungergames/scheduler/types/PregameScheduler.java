package br.com.saintmc.hungergames.scheduler.types;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.event.game.GameStartEvent;
import br.com.saintmc.hungergames.game.GameState;
import br.com.saintmc.hungergames.listener.pregame.PregameListener;
import tk.yallandev.saintmc.common.utils.string.StringUtils;

public class PregameScheduler implements GameSchedule {

	private GameGeneral gameGeneral;
	private List<Listener> listenerList;
	
	public PregameScheduler() {
		this.gameGeneral = GameGeneral.getInstance();
		this.listenerList = Arrays.asList(new PregameListener());
	}

	@EventHandler
	public void onPlayerJoin(PlayerLoginEvent event) {
		if (gameGeneral.getGameState() == GameState.WAITING) {
			gameGeneral.setGameState(GameState.PREGAME);
			gameGeneral.setCountTime(true);

			registerListener();
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		new BukkitRunnable() {

			@Override
			public void run() {
				if (Bukkit.getOnlinePlayers().size() == 0) {
					gameGeneral.setGameState(GameState.WAITING);
					gameGeneral.setCountTime(false);

					unregisterListener();
				}
			}
		}.runTaskLater(GameMain.getInstance(), 7l);
	}

	@Override
	public void pulse(int time, GameState gameState) {
		if (gameState == GameState.WAITING)
			return;

		if (time <= 15 && gameState == GameState.PREGAME) {

			gameGeneral.setGameState(GameState.STARTING);
			gameGeneral.setTime(time);
			
			teleportAll();
		}
		
		if (time <= 0) {
				
			gameGeneral.setGameState(GameState.INVINCIBILITY);
			Bukkit.getPluginManager().callEvent(new GameStartEvent());
			
			unregisterListener();
			unload();
		}
		
		if (time <= 5) {
			Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.NOTE_STICKS, 1f, 1f));
		}

		if ((time % 60 == 0 || (time < 60 && (time % 15 == 0 || time == 10 || time <= 5)))) {
			Bukkit.getOnlinePlayers().forEach(
					p -> p.sendMessage("§9Pregame> §fO jogo iniciará em " + StringUtils.formatTime(time) + "§f!"));
		}
	}

	private void teleportAll() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.teleport(player.getWorld().getSpawnLocation());
		}
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
