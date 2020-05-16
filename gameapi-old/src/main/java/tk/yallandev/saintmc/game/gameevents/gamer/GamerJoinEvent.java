package tk.yallandev.saintmc.game.gameevents.gamer;

import java.util.UUID;

import tk.yallandev.saintmc.game.gameevents.GameEventType;

public class GamerJoinEvent extends GamerEvent {

	public GamerJoinEvent(UUID uuid) {
		super(uuid, GameEventType.PLAYER_JOIN.toString().toLowerCase(), GameEventType.PLAYER_JOIN);
	}

}
