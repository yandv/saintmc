package tk.yallandev.saintmc.game.stage;

public enum GameStage {
	NONE, //
	WAITING(CounterType.STOP, 300), //
	PREGAME(CounterType.COUNTDOWN, 300), //
	STARTING(CounterType.COUNTDOWN, 15), //
	INVINCIBILITY(CounterType.COUNTDOWN, 120), //
	GAMETIME(CounterType.COUNT_UP, INVINCIBILITY.getDefaultTimer()), //
	FINAL, //
	WINNER;

	private int defaultTimer;
	private CounterType defaultType;

	private GameStage() {
		this(CounterType.STOP, 0);
	}

	private GameStage(CounterType type) {
		this(type, 0);
	}

	private GameStage(CounterType type, int timer) {
		defaultType = type;
		defaultTimer = timer;
	}

	public CounterType getDefaultType() {
		return defaultType;
	}

	public int getDefaultTimer() {
		return defaultTimer;
	}

	public static boolean isPregame(GameStage stage) {
		return stage == GameStage.WAITING || stage == GameStage.PREGAME || stage == GameStage.STARTING;
	}

	public static boolean isInvincibility(GameStage stage) {
		return stage == GameStage.INVINCIBILITY;
	}
}
