package tk.yallandev.saintmc.bukkit.event.account;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.NormalEvent;

@Getter
public class PlayerUpdatedFieldEvent extends NormalEvent {
	
	private Player player;
	private BukkitMember bukkitMember;
	private String field;
	private Object oldObject;
	@Setter
	private Object object;

	public PlayerUpdatedFieldEvent(Player p, BukkitMember player, String field, Object oldObject, Object object) {
		this.player = p;
		this.bukkitMember = player;
		this.field = field;
		this.oldObject = oldObject;
		this.object = object;
	}

}
