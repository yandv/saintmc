package tk.yallandev.saintmc.bukkit.event.account;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;

@Getter
public class PlayerUpdateFieldEvent extends PlayerCancellableEvent {

	private BukkitMember bukkitMember;
	private String field;
	@Setter
	private Object oldObject;
	@Setter
	private Object object;

	public PlayerUpdateFieldEvent(Player p, BukkitMember player, String field, Object oldObject, Object object) {
		super(p);
		this.bukkitMember = player;
		this.field = field;
		this.oldObject = oldObject;
		this.object = object;
	}

}
