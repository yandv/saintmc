package tk.yallandev.saintmc.login.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import lombok.Getter;

public class CaptchaListener implements Listener {
	
	@Getter
	public class CaptchaMenu {
		
		private CaptchaHandler captchaHandler;
		
		public CaptchaMenu(Player player, CaptchaHandler captchaHandler) {
			this.captchaHandler = captchaHandler;
		}
		
	}
	
	public interface CaptchaHandler {
		
		void handle(boolean success);
		
	}
	
}
