package tk.yallandev.saintmc.game.gameevents;

public class GameEvent {

	private String name;
	private transient GameEventType type;
	private long time;

	public GameEvent(GameEventType type) {
		this(type.toString().toLowerCase(), type);
	}

	public GameEvent(String name, GameEventType type) {
		this.name = name;
		this.type = type;
		time = System.currentTimeMillis();
	}

	public String getName() {
		return name;
	}

	public GameEventType getType() {
		return type;
	}

	public long getTime() {
		return time;
	}
}
