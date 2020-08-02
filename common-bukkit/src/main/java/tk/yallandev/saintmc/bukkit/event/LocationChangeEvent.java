package tk.yallandev.saintmc.bukkit.event;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;

import lombok.Getter;
import lombok.Setter;

@Getter
public class LocationChangeEvent extends NormalEvent implements Cancellable {

	@Setter
	private boolean cancelled;

	private String configName;

	private Location oldLocation;
	private Location location;

	public LocationChangeEvent(String configName, Location oldLocation, Location location) {
		this.configName = configName;
		this.oldLocation = oldLocation;
		this.location = location;
	}
}
