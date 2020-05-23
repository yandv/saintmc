package br.com.saintmc.hungergames.event.game;

import br.com.saintmc.hungergames.event.GameEvent;
import br.com.saintmc.hungergames.game.GameState;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameStateChangeEvent extends GameEvent {
	
	private GameState fromState;
	private GameState toState;

}
