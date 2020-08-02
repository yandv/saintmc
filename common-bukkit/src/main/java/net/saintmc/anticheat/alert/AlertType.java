package net.saintmc.anticheat.alert;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlertType {

	FLY, SPEED, GLIDE, VELOCITY, HIGH_JUMP, AUTOSOUP(20, -1), NOFALL, MACRO,
	AUTOCLICK(60, 1000l * 60l * 60l * 24l * 30l), VAPE;

	private int maxAlerts;
	private long banTime;

	AlertType() {
		this.maxAlerts = 30;
		this.banTime = 1000l * 60l * 60l * 24l * 30l;
	}

}
