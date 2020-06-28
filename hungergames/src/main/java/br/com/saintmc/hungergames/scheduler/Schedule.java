package br.com.saintmc.hungergames.scheduler;

import br.com.saintmc.hungergames.game.GameState;

public interface Schedule {
	
	void pulse(int time, GameState gameState);

}
