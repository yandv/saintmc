package br.com.saintmc.hungergames.listener.game;

import org.bukkit.event.Listener;

import br.com.saintmc.hungergames.GameMain;

public class GameListener implements Listener {
	
	public GameListener() {
		GameMain.getInstance().registerListener(new CombatlogListener());
	}
	
}
