package tk.yallandev.saintmc.bukkit.event.player;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.NormalEvent;

@Getter
@AllArgsConstructor
public class TeleportAllEvent extends NormalEvent {
	
	private Player target;

}
