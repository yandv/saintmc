package tk.yallandev.saintmc.game.manager;

import java.util.ArrayList;
import java.util.List;

import tk.yallandev.saintmc.game.gameevents.GameEvent;
import tk.yallandev.saintmc.game.gameevents.GameEventType;

public class GameEventManager {
	
	private List<GameEvent> events;

	public GameEventManager() {
		events = new ArrayList<>();
	}

	public void newEvent(GameEventType type) {
		newEvent(type.name().toLowerCase(), type);
	}

	public void newEvent(String name, GameEventType type) {
		newEvent(new GameEvent(name, type));
	}

	public void newEvent(GameEvent event) {
		events.add(event);
	}

}
