package net.saintmc.anticheat.alert.impl;

import net.saintmc.anticheat.alert.Alert;
import net.saintmc.anticheat.alert.AlertType;

public class AlertImpl extends Alert {

	public AlertImpl(AlertType alertType, String playerName) {
		super(alertType, playerName);
	}

	@Override
	public String getMessage() {
		return "§4O " + getPlayerName() + " está usando " + getAlertType().name();
	}

}
