package br.com.saintmc.hungergames.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.event.game.GameInvincibilityEndEvent;
import br.com.saintmc.hungergames.event.game.GameStartEvent;
import br.com.saintmc.hungergames.event.game.GameStateChangeEvent;
import br.com.saintmc.hungergames.listener.SpectatorListener;
import br.com.saintmc.hungergames.scheduler.types.GameScheduler;
import br.com.saintmc.hungergames.scheduler.types.InvincibilityScheduler;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;

public class SchedulerListener implements Listener {
	
	private GameGeneral gameGeneral;

	public SchedulerListener(GameGeneral gameGeneral) {
		this.gameGeneral = gameGeneral;
	}
	
	@EventHandler
	public void onUpdate(UpdateEvent event) {
		
		if (event.getType() != UpdateType.SECOND)
			return;
		
		gameGeneral.pulse();
	}
	
	@EventHandler
	public void onGameStateChange(GameStateChangeEvent event) {
		CommonGeneral.getInstance().getLogger().info(event.getFromState().name() + " > " + event.getToState().name());
	}
	
	@EventHandler
	public void onGameStart(GameStartEvent event) {
		gameGeneral.getSchedulerController().addSchedule(new InvincibilityScheduler());
		
		GameMain.getInstance().registerListener(new SpectatorListener());
		Bukkit.broadcastMessage("§cA partida iniciou!");
	}
	
	@EventHandler
	public void onGameStart(GameInvincibilityEndEvent event) {
		gameGeneral.getSchedulerController().addSchedule(new GameScheduler());
		Bukkit.broadcastMessage("§eA invencibilidade acabou!");
	}
	
}
