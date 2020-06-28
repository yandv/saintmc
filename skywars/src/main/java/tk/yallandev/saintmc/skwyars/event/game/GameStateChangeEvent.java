package tk.yallandev.saintmc.skwyars.event.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.skwyars.event.GameEvent;
import tk.yallandev.saintmc.skwyars.scheduler.MinigameState;

@Getter
@AllArgsConstructor
public class GameStateChangeEvent extends GameEvent {
	
	private MinigameState fromState;
	private MinigameState toState;

}
