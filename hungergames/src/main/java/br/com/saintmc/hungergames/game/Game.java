package br.com.saintmc.hungergames.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class Game {
	
	@Setter
	private long startTime;
	@Setter
	private int startPlayers;

}
