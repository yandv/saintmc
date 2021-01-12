package br.com.saintmc.hungergames.event.game;

import br.com.saintmc.hungergames.event.GameEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameTimeEvent extends GameEvent {
	
	private int time;

}
