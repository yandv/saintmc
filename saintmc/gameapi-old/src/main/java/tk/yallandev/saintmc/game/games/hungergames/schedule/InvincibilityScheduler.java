package tk.yallandev.saintmc.game.games.hungergames.schedule;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import br.com.battlebits.commons.BattlebitsAPI;
import br.com.battlebits.game.games.hungergames.translate.MessageManager;
import br.com.battlebits.game.games.hungergames.translate.MessageManager.CountDownMessageType;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.constructor.ScheduleArgs;
import tk.yallandev.saintmc.game.scheduler.Schedule;
import tk.yallandev.saintmc.game.stage.GameStage;

public class InvincibilityScheduler implements Schedule {

	@Override
	public void pulse(ScheduleArgs args) {
		if (args.getStage() != GameStage.INVINCIBILITY)
			return;
		if (args.getTimer() <= 0) {
			GameMain.getPlugin().setGameStage(GameStage.GAMETIME);
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.ANVIL_LAND, 1, 1);
			}
			return;
		}
		if (args.getTimer() <= 5) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.NOTE_PLING, 1f, 1f);
			}
		}
		if ((args.getTimer() % 60 == 0 || (args.getTimer() < 60 && (args.getTimer() % 15 == 0 || args.getTimer() == 10 || args.getTimer() <= 5)))) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(MessageManager.getCountDownMessage(CountDownMessageType.INVINCIBILITY, BattlebitsAPI.getAccountCommon().getBattlePlayer(p.getUniqueId()).getLanguage(), args.getTimer()));
			}
		}
	}

}
