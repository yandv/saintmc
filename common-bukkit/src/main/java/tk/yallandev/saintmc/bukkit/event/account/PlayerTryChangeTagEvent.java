package tk.yallandev.saintmc.bukkit.event.account;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;
import tk.yallandev.saintmc.common.tag.Tag;

@Getter
public class PlayerTryChangeTagEvent extends PlayerCancellableEvent {
	
	private Tag oldTag;
	@Setter
	private Tag newTag;
	private boolean forced;

	public PlayerTryChangeTagEvent(Player p, Tag oldTag, Tag newTag, boolean forced) {
		super(p);
		this.oldTag = oldTag;
		this.newTag = newTag;
		this.forced = forced;
	}

}
