package tk.yallandev.saintmc.bungee.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

@Getter
@AllArgsConstructor
public class BlockAddressEvent extends Event {
	
	private String ipAddress;
	
	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis() + (1000 * 60 * 60 * 2));
	}

}
