package tk.yallandev.saintmc.game.games.hungergames.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.event.game.GameStageChangeEvent;
import tk.yallandev.saintmc.game.games.hungergames.schedule.GameScheduler;
import tk.yallandev.saintmc.game.games.hungergames.schedule.PregameScheduler;
import tk.yallandev.saintmc.game.stage.GameStage;

public class GameStageChangeListener extends GameListener {

	public GameStageChangeListener(GameMain main) {
		super(main);
	}

	@EventHandler
	public void onGameStageChange(GameStageChangeEvent event) {
		if (event.getLastStage() == GameStage.WAITING) {
			if (event.getNewStage() == GameStage.PREGAME)
				getGameMain().getSchedulerManager().addScheduler("pregamer", new PregameScheduler());
		} else if (event.getNewStage() == GameStage.WAITING) {
			getGameMain().getSchedulerManager().cancelScheduler("pregamer");
			
			if (event.getLastStage() == GameStage.STARTING)
				Bukkit.broadcastMessage("§a§l> §fNão há jogadores suficiente para iniciar a partida!");
			
		} else if (event.getLastStage() == GameStage.INVINCIBILITY) {
			getGameMain().getSchedulerManager().cancelScheduler("invincibility");
			getGameMain().getSchedulerManager().addScheduler("gametime", new GameScheduler());
			Bukkit.broadcastMessage("§a§l> §fA invencibilidade acabou!");
		}
		System.out.println(event.getLastStage().toString() + " > " + event.getNewStage().toString());
	}

}
