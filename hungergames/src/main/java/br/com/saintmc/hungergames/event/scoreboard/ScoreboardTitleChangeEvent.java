package br.com.saintmc.hungergames.event.scoreboard;

import br.com.saintmc.hungergames.event.GameEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ScoreboardTitleChangeEvent extends GameEvent {
	
	private String newTitle;

}
