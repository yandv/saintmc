package net.saintmc.anticheat.alert;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Alert {

	private final AlertType alertType;
	private final String playerName;
	
	private List<AlertMetadata> metadataList = new ArrayList<>();

	private boolean alert;

	public Alert alert() {
		alert = true;
		return this;
	}
	
	public Alert addMetadata(AlertMetadata alertMetadata) {
		metadataList.add(alertMetadata);
		return this;
	}
	
	public boolean hasMetadata() {
		return !metadataList.isEmpty();
	}

	public abstract String getMessage();
}
