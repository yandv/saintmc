package tk.yallandev.saintmc.bukkit.event.account;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.bukkit.account.BukkitMember;

@Getter
public class PlayerUpdatedFieldEvent extends PlayerEvent {

	private static final HandlerList handlers = new HandlerList();

	private BukkitMember bukkitMember;
	private String field;
	@Setter
	private Object object;

	public PlayerUpdatedFieldEvent(Player p, BukkitMember player, String field, Object object) {
		super(p);
		this.bukkitMember = player;
		this.field = field;
		this.object = object;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
