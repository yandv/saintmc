package tk.yallandev.saintmc.bukkit.event.account;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;
import tk.yallandev.saintmc.common.permission.Tag;

@Getter
public class PlayerChangeTagEvent extends PlayerCancellableEvent {

	private Tag oldTag;
	private Tag newTag;
	private boolean isForced;

	public PlayerChangeTagEvent(Player p, Tag oldTag, Tag newTag, boolean isForced) {
		super(p);
		this.oldTag = oldTag;
		this.newTag = newTag;
		this.isForced = isForced;
	}
	
}
