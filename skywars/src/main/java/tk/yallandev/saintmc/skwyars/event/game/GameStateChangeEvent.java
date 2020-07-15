package tk.yallandev.saintmc.skwyars.event.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.skwyars.event.GameEvent;

@Getter
@AllArgsConstructor
public class GameStateChangeEvent extends GameEvent {
	
	private MinigameState fromState;
	private MinigameState toState;

}
