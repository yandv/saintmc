package tk.yallandev.saintmc.bukkit.event.login;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;
import tk.yallandev.saintmc.common.account.Member;

@Getter
public class PlayerChangeLoginStatusEvent extends PlayerCancellableEvent {
	
	private Member member;
	private boolean logged;

	public PlayerChangeLoginStatusEvent(Player p, Member member, boolean newState) {
		super(p);
		this.member = member;
		this.logged = newState;
	}
}
