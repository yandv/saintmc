package tk.yallandev.saintmc.game.gameevents.gamer;

import java.util.UUID;

import tk.yallandev.saintmc.game.gameevents.GameEvent;
import tk.yallandev.saintmc.game.gameevents.GameEventType;

public abstract class GamerEvent extends GameEvent {

	private UUID uniqueId;

	public GamerEvent(UUID uuid, GameEventType type) {
		this(uuid, type.toString().toLowerCase(), type);
	}

	public GamerEvent(UUID uuid, String name, GameEventType type) {
		super(name, type);
		this.uniqueId = uuid;
	}

	public UUID getUniqueId() {
		return uniqueId;
	}

}
