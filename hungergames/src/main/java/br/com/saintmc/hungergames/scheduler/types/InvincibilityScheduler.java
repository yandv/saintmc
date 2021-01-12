package br.com.saintmc.hungergames.scheduler.types;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.event.game.GameInvincibilityEndEvent;
import br.com.saintmc.hungergames.game.GameState;
import br.com.saintmc.hungergames.listener.register.invincibility.InvincibilityListener;
import tk.yallandev.saintmc.common.utils.string.StringUtils;

public class InvincibilityScheduler implements GameSchedule {
	
	private GameGeneral gameGeneral;
	private List<Listener> listenerList;

	public InvincibilityScheduler() {
		this.gameGeneral = GameGeneral.getInstance();
		this.listenerList = Arrays.asList(new InvincibilityListener());
		
		registerListener();
	}

	@Override
	public void pulse(int time, GameState gameState) {
		
		if (time <= 5) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.NOTE_PLING, 1f, 1f);
			}
		}
		if ((time % 60 == 0 || (time < 60 && (time % 15 == 0 || time == 10 || time <= 5)))) {
//			for (Player p : Bukkit.getOnlinePlayers()) {
//				p.sendMessage("§eA invencibilidade acaba em §b" + StringUtils.formatTime(time) + "§f!");
//			}
			
			Bukkit.broadcastMessage("§eA invencibilidade acaba em " + StringUtils.formatTime(time) + "!");
		}
		
		if (time <= 0) {
			
//			Bukkit.broadcastMessage("§a§l❱ §fA invencibilidade acabou!");
			Bukkit.broadcastMessage("§cA invencibilidade acabou!");
			Bukkit.getPluginManager().callEvent(new GameInvincibilityEndEvent());
			gameGeneral.setGameState(GameState.GAMETIME);
			
			unregisterListener();
			unload();
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
