package tk.yallandev.saintmc.skwyars.event.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.skwyars.event.GameEvent;

@Getter
@AllArgsConstructor
public class GameTimeEvent extends GameEvent {
	
	private int time;

}
