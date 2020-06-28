package tk.yallandev.saintmc.bukkit.event.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.NormalEvent;

@Getter
@AllArgsConstructor
public class PlayerChangeEvent extends NormalEvent {

	private int totalMembers;

}
