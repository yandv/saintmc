package tk.yallandev.saintmc.game.gameevents.gamer;

import java.util.UUID;

import tk.yallandev.saintmc.game.gameevents.GameEventType;

public class GamerQuitEvent extends GamerEvent {

	public GamerQuitEvent(UUID uuid) {
		super(uuid, GameEventType.PLAYER_LEAVE.toString().toLowerCase(), GameEventType.PLAYER_LEAVE);
	}

}
