package br.com.saintmc.hungergames.listener.register;

import org.bukkit.event.EventHandler;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.event.game.GameStateChangeEvent;
import br.com.saintmc.hungergames.event.game.GameTimeEvent;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;

public class UpdateListener extends GameListener {

	@EventHandler
	public void onGameStateChange(GameStateChangeEvent event) {
		CommonGeneral.getInstance().getServerData().updateStatus(
				MinigameState.valueOf(GameGeneral.getInstance().getGameState().toString()),
				GameGeneral.getInstance().getTime());
	}

	@EventHandler
	public void onGameTime(GameTimeEvent event) {
		CommonGeneral.getInstance().getServerData().updateStatus(
				MinigameState.valueOf(GameGeneral.getInstance().getGameState().toString()),
				GameGeneral.getInstance().getTime());
	}

}
