package tk.yallandev.saintmc.bungee.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.plugin.Event;
import tk.yallandev.saintmc.bungee.bungee.BungeeMember;

@AllArgsConstructor
@Getter
public class PlayerUpdateFieldEvent extends Event {
	
	private BungeeMember bungeeMember;
	private String field;
	@Setter
	private Object oldObject;
	@Setter
	private Object object;

}
