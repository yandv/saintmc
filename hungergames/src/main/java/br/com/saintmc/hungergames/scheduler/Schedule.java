package br.com.saintmc.hungergames.scheduler;

import org.bukkit.event.Listener;

import br.com.saintmc.hungergames.game.GameState;

public interface Schedule extends Listener {
	
	void pulse(int time, GameState gameState);

}
