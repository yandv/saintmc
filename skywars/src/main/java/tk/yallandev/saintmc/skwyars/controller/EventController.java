package tk.yallandev.saintmc.skwyars.controller;

import lombok.Getter;
import tk.yallandev.saintmc.skwyars.game.EventType;

@Getter
public class EventController {

	private EventType eventType;
	private int eventTime;

	public EventController() {
		eventType = EventType.REFIL;
		eventTime = 180;
	}

	/**
	 * 
	 * Change the next event
	 * 
	 * Time in seconds
	 * 
	 * @param eventType
	 * @param eventTime
	 */

	public void setEventType(EventType eventType, int eventTime) {
		this.eventType = eventType;
		this.eventTime = eventTime;
	}

	public boolean pulse() {
		this.eventTime--;

		return eventTime <= 0;
	}

}
