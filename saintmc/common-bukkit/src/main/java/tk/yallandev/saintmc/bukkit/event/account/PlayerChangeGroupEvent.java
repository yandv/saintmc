package tk.yallandev.saintmc.bukkit.event.account;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.account.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;
import tk.yallandev.saintmc.common.permission.Group;

@Getter
public class PlayerChangeGroupEvent extends PlayerCancellableEvent {
	
	private BukkitMember bukkitMember;
	private Group group;

	public PlayerChangeGroupEvent(Player p, BukkitMember player, Group group) {
		super(p);
		this.bukkitMember = player;
		this.group = group;
	}
}
