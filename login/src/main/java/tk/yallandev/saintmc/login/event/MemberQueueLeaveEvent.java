package tk.yallandev.saintmc.login.event;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.bukkit.account.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.NormalEvent;

@Getter
@AllArgsConstructor
public class MemberQueueLeaveEvent extends NormalEvent {
	
	private Player player;
	private BukkitMember member;

}
