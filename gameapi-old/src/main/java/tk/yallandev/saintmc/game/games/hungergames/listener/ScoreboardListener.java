package tk.yallandev.saintmc.game.games.hungergames.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.event.admin.PlayerAdminModeEvent;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.event.game.GameTimerEvent;
import tk.yallandev.saintmc.game.event.player.PlayerSelectedKitEvent;
import tk.yallandev.saintmc.game.games.hungergames.HungerGamesMode;

public class ScoreboardListener extends tk.yallandev.saintmc.game.listener.GameListener {

	private HungerGamesMode mode;

	public ScoreboardListener(GameMain main, HungerGamesMode mode) {
		super(main);
		this.mode = mode;
	}

//	private int i = 0;

//	@EventHandler
//	public void onUpdate(UpdateEvent event) {
//		if (event.getType() != UpdateType.TICK)
//			return;
//		++i;
//		
//		if (i % 7 == 0) {
//			i = 0;
//			mode.getScoreBoardManager().updateTitleText();
//			
//			for (Player p : Bukkit.getOnlinePlayers()) {
//				mode.getScoreBoardManager().updateTitle(p);
//			}
//		}
//	}

	@EventHandler
	public void onPlayerSelectedKit(PlayerSelectedKitEvent event) {
//		mode.getScoreBoardManager().updatePlayerKit(event.getPlayer());
		BukkitMain.getInstance().getScoreboardManager().updateScoreboard(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
//		mode.getScoreBoardManager().createScoreboard(event.getPlayer());
		mode.getScoreBoardManager().updatePlayersLeft();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
//		mode.getScoreBoardManager().updatePlayersLeft();
		for (Player player : Bukkit.getOnlinePlayers()) {
			BukkitMain.getInstance().getScoreboardManager().updateScoreboard(player);
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			BukkitMain.getInstance().getScoreboardManager().updateScoreboard(player);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAdminMode(PlayerAdminModeEvent event) {
		new BukkitRunnable() {

			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					BukkitMain.getInstance().getScoreboardManager().updateScoreboard(player);
				}
			}
		}.runTaskLater(GameMain.getPlugin(), 7l);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (event.getEntity().getKiller() != null)
			BukkitMain.getInstance().getScoreboardManager().updateScoreboard(event.getEntity().getKiller());
	}

	@EventHandler
	public void onGameStage(GameTimerEvent event) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			BukkitMain.getInstance().getScoreboardManager().updateScoreboard(player);
		}
//		mode.getScoreBoardManager().updateTimer();
	}

}
