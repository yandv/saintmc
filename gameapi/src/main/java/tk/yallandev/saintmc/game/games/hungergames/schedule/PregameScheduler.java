package tk.yallandev.saintmc.game.games.hungergames.schedule;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.common.utils.string.StringUtils;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.constructor.ScheduleArgs;
import tk.yallandev.saintmc.game.games.hungergames.manager.ColiseumManager;
import tk.yallandev.saintmc.game.scheduler.Schedule;
import tk.yallandev.saintmc.game.stage.GameStage;

public class PregameScheduler implements Schedule {

	private ColiseumManager manager;

	public PregameScheduler() {
		manager = new ColiseumManager();
	}

	@Override
	public void pulse(ScheduleArgs args) {
		if (!(args.getStage() == GameStage.PREGAME || args.getStage() == GameStage.STARTING))
			return;
		
		if (args.getTimer() <= 60) {
			if (args.getTimer() > 30) {
				manager.teleportRecursive(args.getTimer() - 30);
			} else {
				manager.teleportOutsidePlayers();
			}
			if (manager.isDoorsOpen())
				manager.closeDoors();
		} else {
			if (!manager.isDoorsOpen())
				manager.openDoors();
		}
		
		if (args.getTimer() <= 15 && args.getStage() == GameStage.PREGAME) {
			GameMain.getPlugin().setGameStage(GameStage.STARTING);
			GameMain.getPlugin().setTimer(args.getTimer());
		} else if (args.getTimer() <= 0) {
			if (GameMain.getPlugin().playersLeft() >= 1) {
				GameMain.getPlugin().startGame();
			} else {
				if (args.getTimer() < 60)
					GameMain.getPlugin().setGameStage(GameStage.WAITING, 60);
				else if (args.getTimer() < 120)
					GameMain.getPlugin().setGameStage(GameStage.WAITING, 120);
				else if (args.getTimer() < 180)
					GameMain.getPlugin().setGameStage(GameStage.WAITING, 180);
				else if (args.getTimer() < 240)
					GameMain.getPlugin().setGameStage(GameStage.WAITING, 240);
				else
					GameMain.getPlugin().setGameStage(GameStage.WAITING);
			}
			return;
		}
		
		if (args.getTimer() <= 5) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.NOTE_STICKS, 1f, 1f);
			}
		}
		
		if ((args.getTimer() % 60 == 0 || (args.getTimer() < 60 && (args.getTimer() % 15 == 0 || args.getTimer() == 10 || args.getTimer() <= 5)))) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage("§9Pregame> §fO jogo iniciará em " + StringUtils.formatTime(args.getTimer()) + "§f!");
			}
		}
	}

}
