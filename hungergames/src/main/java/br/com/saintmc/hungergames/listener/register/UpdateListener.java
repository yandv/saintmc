package br.com.saintmc.hungergames.listener.register;

import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.saintmc.hungergames.event.game.GameStateChangeEvent;
import br.com.saintmc.hungergames.event.game.GameTimeEvent;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;

public class UpdateListener extends br.com.saintmc.hungergames.listener.GameListener {

	@EventHandler
	public void onGameStateChange(GameStateChangeEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				CommonGeneral.getInstance().getServerData().updateStatus(
						MinigameState.valueOf(getGameGeneral().getGameState().toString()), getGameGeneral().getTime());
			}
		}.runTaskLaterAsynchronously(getGameMain(), 1);
	}

	@EventHandler
	public void onGameTime(GameTimeEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				CommonGeneral.getInstance().getServerData().updateStatus(
						MinigameState.valueOf(getGameGeneral().getGameState().toString()), getGameGeneral().getTime());
			}
		}.runTaskLaterAsynchronously(getGameMain(), 1);
	}

}
