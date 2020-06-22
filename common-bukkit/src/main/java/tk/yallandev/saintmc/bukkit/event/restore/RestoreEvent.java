package tk.yallandev.saintmc.bukkit.event.restore;

import org.bukkit.event.Cancellable;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.bukkit.event.NormalEvent;

public class RestoreEvent extends NormalEvent implements Cancellable {
	
	@Setter
	@Getter
	private boolean cancelled;

}
