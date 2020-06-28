package tk.yallandev.saintmc.login.event.captcha;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;

public class CaptchaSuccessEvent extends PlayerCancellableEvent {

	public CaptchaSuccessEvent(Player player) {
		super(player);
	}

}
