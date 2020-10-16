package tk.yallandev.saintmc.skwyars.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SkywarsType {
	
	SOLO(12), TEAM(24), SQUAD(64);
	
	private int maxPlayers;

}