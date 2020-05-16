package tk.yallandev.saintmc.bukkit.event.login;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;
import tk.yallandev.saintmc.common.account.Member;

@Getter
public class PlayerRegisterEvent extends PlayerCancellableEvent {
	
	private Member member;

	public PlayerRegisterEvent(Player p, Member member) {
		super(p);
		this.member = member;
	}
}
