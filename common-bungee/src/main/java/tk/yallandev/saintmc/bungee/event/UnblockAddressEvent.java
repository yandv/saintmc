package tk.yallandev.saintmc.bungee.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

@Getter
@AllArgsConstructor
public class UnblockAddressEvent extends Event {
	
	private String ipAddress;

}
