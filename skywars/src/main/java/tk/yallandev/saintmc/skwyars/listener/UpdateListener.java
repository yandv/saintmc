package tk.yallandev.saintmc.skwyars.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.skwyars.GameGeneral;
import tk.yallandev.saintmc.skwyars.GameMain;
import tk.yallandev.saintmc.skwyars.event.game.GameStateChangeEvent;
import tk.yallandev.saintmc.skwyars.event.game.GameTimeEvent;

public class UpdateListener implements Listener {

	/* send the information with redis for all ?? */

	@EventHandler
	public void onGameStateChange(GameStateChangeEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				CommonGeneral.getInstance().getServerData().updateStatus(
						MinigameState.valueOf(GameGeneral.getInstance().getMinigameState().name()),
						GameGeneral.getInstance().getTime());
			}
		}.runTaskAsynchronously(GameMain.getInstance());
	}

	@EventHandler
	public void onGameTime(GameTimeEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				CommonGeneral.getInstance().getServerData().updateStatus(
						MinigameState.valueOf(GameGeneral.getInstance().getMinigameState().name()),
						GameMain.getInstance().getMapName(), GameGeneral.getInstance().getTime());
			}
		}.runTaskAsynchronously(GameMain.getInstance());
	}

}
