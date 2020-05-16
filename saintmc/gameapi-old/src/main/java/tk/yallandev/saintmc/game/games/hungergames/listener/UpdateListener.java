package tk.yallandev.saintmc.game.games.hungergames.listener;

import org.bukkit.event.EventHandler;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.event.game.GameStageChangeEvent;
import tk.yallandev.saintmc.game.event.game.GameTimerEvent;
import tk.yallandev.saintmc.game.stage.GameStage;

public class UpdateListener extends GameListener {

	public UpdateListener(GameMain main) {
		super(main);
	}

	@EventHandler
	public void onStart(GameStageChangeEvent event) {
		if (event.getNewStage() == GameStage.WAITING) {
			CommonGeneral.getInstance().getServerData().updateStatus(MinigameState.WAITING, GameMain.getPlugin().getTimer());
		} else if (GameStage.isPregame(event.getNewStage())) {
			CommonGeneral.getInstance().getServerData().updateStatus(MinigameState.PREGAME, GameMain.getPlugin().getTimer());
		} else if (GameStage.isInvincibility(event.getNewStage())) {
			CommonGeneral.getInstance().getServerData().updateStatus(MinigameState.INVINCIBILITY, GameMain.getPlugin().getTimer());
		} else {
			CommonGeneral.getInstance().getServerData().updateStatus(MinigameState.GAMETIME, GameMain.getPlugin().getTimer());
		}
	}

	@EventHandler
	public void onChange(GameTimerEvent event) {
		CommonGeneral.getInstance().getServerData().updateStatus(MinigameState.valueOf(GameMain.getPlugin().getGameStage().toString()), GameMain.getPlugin().getTimer());
	}

}
